package com.nearpick.app.common.protection

import com.nearpick.app.domain.purchase.protection.ConsumerProtectionState
import com.nearpick.app.domain.purchase.protection.ConsumerProtectionStateProvider
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@Component
class ConsumerHealthMonitor(
    private val meterRegistry: MeterRegistry,
    private val registry: KafkaListenerEndpointRegistry
) : ConsumerProtectionStateProvider {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val CONSUMER_CONTAINER_ID = "purchaseConsumer"

        // 진입 임계값
        private const val THROTTLE_CONN_RATIO = 0.6
        private const val PAUSE_CONN_RATIO = 0.8
        private const val PAUSE_PENDING_THRESHOLD = 5.0

        // 해제 임계값 (히스테리시스)
        private const val RELEASE_CONN_RATIO = 0.5
        private const val RELEASE_PENDING_THRESHOLD = 0.0

        // PAUSED → THROTTLED 전이에 필요한 연속 정상 횟수
        private const val RECOVERY_CONSECUTIVE_COUNT = 3

        // Lag 보조 판단 임계값
        private const val HIGH_LAG_THRESHOLD = 5000L
    }

    private val currentState = AtomicReference(ConsumerProtectionState.NORMAL)
    private val consecutiveHealthyCount = AtomicInteger(0)
    private val lastConnRatio = AtomicReference(0.0)
    private val lastPending = AtomicReference(0.0)

    private lateinit var pauseCounter: Counter
    private lateinit var throttleCounter: Counter

    @PostConstruct
    fun initMetrics() {
        meterRegistry.gauge("consumer_protection_state", currentState) { it.get().code.toDouble() }
        meterRegistry.gauge("consumer_protection_db_conn_ratio", lastConnRatio) { it.get() }
        meterRegistry.gauge("consumer_protection_db_pending", lastPending) { it.get() }

        pauseCounter = Counter.builder("consumer_protection_pause_total")
            .description("Total times consumer entered PAUSED state")
            .register(meterRegistry)

        throttleCounter = Counter.builder("consumer_protection_throttle_total")
            .description("Total times consumer entered THROTTLED state")
            .register(meterRegistry)
    }

    override fun getState(): ConsumerProtectionState = currentState.get()

    @Scheduled(fixedDelay = 5000)
    fun monitor() {
        val health = probeDbHealth()
        val lag = readConsumerLag()
        val newState = determineState(health, lag)
        val prevState = currentState.get()

        if (newState == prevState) {
            if (prevState == ConsumerProtectionState.PAUSED && isHealthy(health)) {
                consecutiveHealthyCount.incrementAndGet()
            } else if (prevState == ConsumerProtectionState.PAUSED) {
                consecutiveHealthyCount.set(0)
            }
            return
        }

        applyTransition(prevState, newState, health, lag)
    }

    private fun determineState(health: DbHealth, lag: Long): ConsumerProtectionState {
        val prevState = currentState.get()

        // PAUSED 진입 조건
        if (health.connRatio > PAUSE_CONN_RATIO || health.pending > PAUSE_PENDING_THRESHOLD) {
            return ConsumerProtectionState.PAUSED
        }

        // THROTTLED 진입 조건
        if (health.connRatio > THROTTLE_CONN_RATIO) {
            // THROTTLED 상태에서 Lag까지 높으면 → PAUSED 격상
            if (lag > HIGH_LAG_THRESHOLD && prevState == ConsumerProtectionState.THROTTLED) {
                return ConsumerProtectionState.PAUSED
            }
            return ConsumerProtectionState.THROTTLED
        }

        // 해제 조건 (히스테리시스)
        return when (prevState) {
            ConsumerProtectionState.PAUSED -> {
                if (isHealthy(health) && consecutiveHealthyCount.get() >= RECOVERY_CONSECUTIVE_COUNT) {
                    ConsumerProtectionState.THROTTLED
                } else {
                    ConsumerProtectionState.PAUSED
                }
            }
            ConsumerProtectionState.THROTTLED -> {
                if (isHealthy(health)) {
                    ConsumerProtectionState.NORMAL
                } else {
                    ConsumerProtectionState.THROTTLED
                }
            }
            ConsumerProtectionState.NORMAL -> ConsumerProtectionState.NORMAL
        }
    }

    private fun isHealthy(health: DbHealth): Boolean {
        return health.connRatio < RELEASE_CONN_RATIO && health.pending <= RELEASE_PENDING_THRESHOLD
    }

    private fun applyTransition(
        prev: ConsumerProtectionState,
        next: ConsumerProtectionState,
        health: DbHealth,
        lag: Long
    ) {
        when {
            next == ConsumerProtectionState.PAUSED && prev != ConsumerProtectionState.PAUSED -> {
                pauseConsumer()
                consecutiveHealthyCount.set(0)
                pauseCounter.increment()
                log.warn(
                    "[ConsumerProtection] {} → PAUSED. connRatio={}, pending={}, lag={}",
                    prev, health.connRatio, health.pending, lag
                )
            }

            prev == ConsumerProtectionState.PAUSED && next == ConsumerProtectionState.THROTTLED -> {
                resumeConsumer()
                consecutiveHealthyCount.set(0)
                throttleCounter.increment()
                log.info(
                    "[ConsumerProtection] PAUSED → THROTTLED. {}회 연속 정상 확인. connRatio={}, pending={}",
                    RECOVERY_CONSECUTIVE_COUNT, health.connRatio, health.pending
                )
            }

            prev == ConsumerProtectionState.NORMAL && next == ConsumerProtectionState.THROTTLED -> {
                throttleCounter.increment()
                log.warn(
                    "[ConsumerProtection] NORMAL → THROTTLED. connRatio={}, pending={}, lag={}",
                    health.connRatio, health.pending, lag
                )
            }

            next == ConsumerProtectionState.NORMAL && prev != ConsumerProtectionState.NORMAL -> {
                log.info(
                    "[ConsumerProtection] {} → NORMAL. connRatio={}, pending={}",
                    prev, health.connRatio, health.pending
                )
            }
        }

        currentState.set(next)
    }

    private fun pauseConsumer() {
        try {
            val container = registry.getListenerContainer(CONSUMER_CONTAINER_ID)
            if (container != null && !container.isContainerPaused) {
                container.pause()
            }
        } catch (e: Exception) {
            log.error("[ConsumerProtection] Consumer pause 실패", e)
        }
    }

    private fun resumeConsumer() {
        try {
            val container = registry.getListenerContainer(CONSUMER_CONTAINER_ID)
            if (container != null && container.isContainerPaused) {
                container.resume()
            }
        } catch (e: Exception) {
            log.error("[ConsumerProtection] Consumer resume 실패", e)
        }
    }

    private fun probeDbHealth(): DbHealth {
        val active = readGauge("hikaricp.connections.active")
        val max = readGauge("hikaricp.connections.max")
        val pending = readGauge("hikaricp.connections.pending")

        val ratio = if (max > 0) active / max else 0.0
        lastConnRatio.set(ratio)
        lastPending.set(pending)

        return DbHealth(connRatio = ratio, pending = pending)
    }

    private fun readConsumerLag(): Long {
        return try {
            meterRegistry.find("kafka.consumer.records.lag.max")
                .gauge()?.value()?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    private fun readGauge(name: String): Double {
        return try {
            meterRegistry.find(name).gauge()?.value() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    data class DbHealth(
        val connRatio: Double,
        val pending: Double
    )
}
