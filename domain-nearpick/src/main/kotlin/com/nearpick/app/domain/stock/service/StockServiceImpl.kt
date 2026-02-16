package com.nearpick.app.domain.stock.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nearpick.app.common.exception.CurrentProductDataNotFoundException
import com.nearpick.app.common.exception.CurrentProductStockDataNotFoundException
import com.nearpick.app.domain.product.dto.ProductFirstComeCache
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.product.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate

@Service
@Transactional(readOnly = false)
open class StockServiceImpl(
    private val redisTemplate: StringRedisTemplate,
    private val productRepository: ProductRepository,
    private val objectMapper: ObjectMapper
) : StockService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    override fun getStockVersionKey(productId: String): String =
        "stock:v${LocalDate.now()}:$productId"

    @Transactional(readOnly = true)
    override fun getProductVersionKey(productId: String): String =
        "product:v${LocalDate.now()}:$productId"

    override fun initializeDailyStock() {
        val nextVersion = "v${LocalDate.now().plusDays(1)}"

        val products = productRepository.findAllByProductTypeAndStatus(ProductType.FIRST_COME, ProductStatus.ACTIVE)

        initializeForVersion(products, nextVersion)
        initializeProductCache(products, nextVersion)
    }

    fun initializeForVersion(products: List<ProductEntity>, version: String) {
        val stockMap = products.associate { product ->
            val key = "stock:$version:${product.id}"
            key to product.stock.toString()
        }

        redisTemplate.opsForValue().multiSet(stockMap)

        redisTemplate.executePipelined { connection ->
            val expireAt = Duration.ofDays(2)
            stockMap.keys.forEach { key ->
                connection.keyCommands().expire(key.toByteArray(), expireAt.seconds)
            }
            null
        }
    }

    fun initializeProductCache(products: List<ProductEntity>, version: String) {
        val cacheMap: Map<String, String> = products.associate { product ->
            val key = "product:${version}:${product.id}"
            val dto = ProductFirstComeCache(
                product.price,
                product.productType,
                product.startDt,
                product.endDt,
                product.status
            )

            key to objectMapper.writeValueAsString(dto)
        }

        redisTemplate.opsForValue().multiSet(cacheMap)

        redisTemplate.executePipelined { connection ->
            val expireAt = Duration.ofDays(2)
            cacheMap.keys.forEach { key ->
                connection.keyCommands().expire(key.toByteArray(), expireAt.seconds)
            }
            null
        }
    }

    override fun getProductCache(productId: String): ProductFirstComeCache {
        val key = getProductVersionKey(productId)
        if (redisTemplate.hasKey(key)) {
            return objectMapper.readValue(redisTemplate.opsForValue().get(key), ProductFirstComeCache::class.java)
        } else {
            throw CurrentProductDataNotFoundException(productId)
        }
    }

    override fun getStock(productId: String): Int {
        val key = getStockVersionKey(productId)
        if (redisTemplate.hasKey(key)) {
            return redisTemplate.opsForValue().get(key)?.toInt() ?:  0
        } else {
            throw CurrentProductStockDataNotFoundException(productId)
        }
    }

    override fun decreaseStockIfAvailable(productId: String, quantity: Int): Boolean {
        val key = getStockVersionKey(productId)

        val script = """
            local stock = tonumber(redis.call('GET', KEYS[1]))
            local qty = tonumber(ARGV[1])
            if stock and stock >= qty then
                redis.call('DECRBY', KEYS[1], qty)
                return 1
            else
                return 0
            end
        """.trimIndent()

        val result = redisTemplate.execute(
            DefaultRedisScript(script, Long::class.java),
            listOf(key),
            quantity.toString()
        )
        return result == 1L
    }

    override fun recoverStock(productId: String, quantity: Int) {
        redisTemplate.opsForValue().increment(getStockVersionKey(productId), quantity.toLong())
    }

    /**
     * Redis stock > DB stock 인 경우에만 Redis를 DB 값으로 감소시킨다.
     * Redis stock <= DB stock 인 경우에는 아무 것도 하지 않는다. (oversell 방지 우선)
     */
    private val reconcileScript = DefaultRedisScript<Long>(
        """
        local current = tonumber(redis.call('GET', KEYS[1]))
        if current == nil then
            return -1
        end
        local dbStock = tonumber(ARGV[1])
        if current > dbStock then
            redis.call('SET', KEYS[1], ARGV[1])
            return 1
        end
        return 0
        """.trimIndent(),
        Long::class.java
    )

    @Transactional(readOnly = true)
    override fun reconcileAll(): ReconciliationResult {
        val products = productRepository.findAllByProductTypeAndStatus(
            ProductType.FIRST_COME, ProductStatus.ACTIVE
        )

        var corrected = 0

        for (product in products) {
            val dbStock = product.stock ?: 0
            val redisKey = getStockVersionKey(product.id)
            val redisValueBefore = redisTemplate.opsForValue().get(redisKey)

            val result = redisTemplate.execute(
                reconcileScript,
                listOf(redisKey),
                dbStock.toString()
            )

            when (result) {
                1L -> {
                    log.warn(
                        "[Reconciliation] 보정 완료. productId={}, redis={}→{}, db={}",
                        product.id, redisValueBefore, dbStock, dbStock
                    )
                    corrected++
                }
                -1L -> {
                    log.info(
                        "[Reconciliation] Redis 키 없음 (TTL 만료 등). productId={}, skip",
                        product.id
                    )
                }
            }
        }

        return ReconciliationResult(checked = products.size, corrected = corrected)
    }
}
