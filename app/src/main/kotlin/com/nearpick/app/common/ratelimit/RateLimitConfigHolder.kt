package com.nearpick.app.common.ratelimit

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class RateLimitConfigHolder(
    @Qualifier("rateLimitRedisTemplate")
    private val rateLimitRedis: StringRedisTemplate,
    private val rateLimitRedisMessageListenerContainer: RedisMessageListenerContainer,
    @Value("\${ratelimit.global-tps}") private val defaultGlobalTps: Int,
    @Value("\${ratelimit.per-user-tps}") val perUserTps: Int,
    @Value("\${ratelimit.config-key}") private val configKey: String,
    @Value("\${ratelimit.config-channel}") private val configChannel: String
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val currentMax = AtomicInteger(0)

    @PostConstruct
    fun init() {
        currentMax.set(defaultGlobalTps)
        loadFromRedis()
        subscribeToPubSub()
    }

    private fun loadFromRedis() {
        try {
            val saved = rateLimitRedis.opsForValue().get(configKey)
            val parsed = saved?.toIntOrNull()
            if (parsed != null && parsed > 0) {
                currentMax.set(parsed)
                log.info("[RateLimitConfig] Redis에서 초기값 로드. max={}", parsed)
            } else {
                log.info("[RateLimitConfig] Redis에 설정값 없음. 기본값 사용. max={}", defaultGlobalTps)
            }
        } catch (e: Exception) {
            log.warn("[RateLimitConfig] Redis 초기 로드 실패. 기본값 사용. max={}", defaultGlobalTps, e)
        }
    }

    private fun subscribeToPubSub() {
        try {
            val listener = MessageListener { message, _ ->
                val newValue = String(message.body).toIntOrNull()
                if (newValue != null && newValue > 0) {
                    val prev = currentMax.getAndSet(newValue)
                    log.info("[RateLimitConfig] Pub/Sub 수신. max 변경: {} → {}", prev, newValue)
                } else {
                    log.warn("[RateLimitConfig] Pub/Sub 수신했으나 유효하지 않은 값: {}", String(message.body))
                }
            }
            rateLimitRedisMessageListenerContainer.addMessageListener(listener, ChannelTopic(configChannel))
            log.info("[RateLimitConfig] Pub/Sub 구독 시작. channel={}", configChannel)
        } catch (e: Exception) {
            log.warn("[RateLimitConfig] Pub/Sub 구독 실패. fallback polling에 의존.", e)
        }
    }

    @Scheduled(fixedDelay = 30_000)
    fun syncFromRedis() {
        try {
            val saved = rateLimitRedis.opsForValue().get(configKey)
            val parsed = saved?.toIntOrNull()
            if (parsed != null && parsed > 0) {
                val prev = currentMax.get()
                if (prev != parsed) {
                    currentMax.set(parsed)
                    log.info("[RateLimitConfig] Polling 동기화. max 변경: {} → {}", prev, parsed)
                }
            }
        } catch (e: Exception) {
            log.debug("[RateLimitConfig] Polling 동기화 실패. 현재 값 유지. max={}", currentMax.get())
        }
    }

    fun getMax(): Int = currentMax.get()
}
