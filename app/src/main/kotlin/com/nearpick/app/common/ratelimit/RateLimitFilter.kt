package com.nearpick.app.common.ratelimit

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.atomic.AtomicBoolean

@Component
class RateLimitFilter(
    @Qualifier("rateLimitRedisTemplate")
    private val rateLimitRedis: StringRedisTemplate,
    private val configHolder: RateLimitConfigHolder,
    private val localFallbackLimiter: LocalFallbackLimiter,
    private val meterRegistry: MeterRegistry,
    @Value("\${ratelimit.target-path-prefix}") private val targetPathPrefix: String
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)
    private val fallbackActive = AtomicBoolean(false)

    private lateinit var totalCounter: Counter
    private lateinit var rejectedCounter: Counter

    companion object {
        private const val WINDOW_TTL_SECONDS = 2L

        private val SLIDING_WINDOW_SCRIPT = DefaultRedisScript<Long>(
            """
            local current = redis.call('INCR', KEYS[1])
            if current == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[2])
            end
            if current > tonumber(ARGV[1]) then
                return 0
            end
            return 1
            """.trimIndent(),
            Long::class.java
        )
    }

    @PostConstruct
    fun initMetrics() {
        totalCounter = Counter.builder("ratelimit_purchase_total")
            .description("Total purchase rate limit checks")
            .register(meterRegistry)

        rejectedCounter = Counter.builder("ratelimit_purchase_rejected")
            .description("Rejected purchase requests by rate limit")
            .register(meterRegistry)

        meterRegistry.gauge("ratelimit_fallback_active", fallbackActive) { if (it.get()) 1.0 else 0.0 }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!request.requestURI.startsWith(targetPathPrefix) || request.method != "POST") {
            filterChain.doFilter(request, response)
            return
        }

        totalCounter.increment()

        if (!checkGlobalLimit()) {
            writeRateLimitResponse(response, "서버가 혼잡합니다. 잠시 후 다시 시도해주세요.")
            rejectedCounter.increment()
            return
        }

        val userId = extractUserId()
        if (userId != null && !checkPerUserLimit(userId)) {
            writeRateLimitResponse(response, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.")
            rejectedCounter.increment()
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun checkGlobalLimit(): Boolean {
        val max = configHolder.getMax()
        return tryRedisRateLimit("ratelimit:purchase", max)
    }

    private fun checkPerUserLimit(userId: String): Boolean {
        val max = configHolder.perUserTps
        return tryRedisRateLimit("ratelimit:user:$userId", max)
    }

    private fun tryRedisRateLimit(keyPrefix: String, max: Int): Boolean {
        return try {
            val epochSecond = System.currentTimeMillis() / 1000
            val key = "$keyPrefix:$epochSecond"

            val result = rateLimitRedis.execute(
                SLIDING_WINDOW_SCRIPT,
                listOf(key),
                max.toString(),
                WINDOW_TTL_SECONDS.toString()
            )

            if (fallbackActive.getAndSet(false)) {
                log.info("[RateLimit] Redis 복구 감지. 글로벌 Rate Limit 복귀.")
            }

            result == 1L
        } catch (e: Exception) {
            if (!fallbackActive.getAndSet(true)) {
                log.warn("[RateLimit] Redis 장애 감지. 로컬 Fallback 전환.", e)
            }
            localFallbackLimiter.tryAcquire()
        }
    }

    private fun extractUserId(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        return auth?.name
    }

    private fun writeRateLimitResponse(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_SERVICE_UNAVAILABLE
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.setHeader("Retry-After", "1")
        response.writer.write("""{"status":429,"message":"$message"}""")
    }
}
