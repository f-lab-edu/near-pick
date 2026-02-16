package com.nearpick.app.common.ratelimit

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@Component
class LocalFallbackLimiter(
    private val instanceCountProvider: InstanceCountProvider,
    @Value("\${ratelimit.degraded-global-tps}") private val degradedGlobalTps: Int,
    @Value("\${ratelimit.safety-factor}") private val safetyFactor: Double,
    @Value("\${ratelimit.absolute-max-local-tps}") private val absoluteMaxLocalTps: Int
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val counter = AtomicInteger(0)
    private val windowStart = AtomicLong(0)

    fun tryAcquire(): Boolean {
        val now = System.currentTimeMillis() / 1000
        val currentWindowStart = windowStart.get()

        if (now != currentWindowStart) {
            if (windowStart.compareAndSet(currentWindowStart, now)) {
                counter.set(0)
            }
        }

        val localLimit = calculateLocalLimit()
        val current = counter.incrementAndGet()
        return current <= localLimit
    }

    fun calculateLocalLimit(): Int {
        val instanceCount = instanceCountProvider.getCount()
        val computed = (degradedGlobalTps.toDouble() / instanceCount * safetyFactor).toInt()
        return computed.coerceAtMost(absoluteMaxLocalTps).coerceAtLeast(1)
    }
}
