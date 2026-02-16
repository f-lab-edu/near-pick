package com.nearpick.app.common.ratelimit

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.atomic.AtomicInteger

@Component
class InstanceCountProvider(
    @Value("\${instance-count.namespace}") private val namespace: String,
    @Value("\${instance-count.service-name}") private val serviceName: String,
    @Value("\${ratelimit.fallback-instance-count}") private val fallbackCount: Int
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val activeCount = AtomicInteger(fallbackCount)

    private val k8sClient: WebClient = WebClient.builder()
        .baseUrl("https://kubernetes.default.svc")
        .build()

    @PostConstruct
    fun init() {
        refreshInstanceCount()
    }

    @Scheduled(fixedDelayString = "\${instance-count.refresh-interval-ms}")
    fun refreshInstanceCount() {
        try {
            val response = k8sClient.get()
                .uri("/api/v1/namespaces/{ns}/endpoints/{svc}", namespace, serviceName)
                .header("Authorization", "Bearer ${loadServiceAccountToken()}")
                .retrieve()
                .bodyToMono(Map::class.java)
                .block()

            val count = countReadyAddresses(response)
            if (count > 0) {
                val prev = activeCount.getAndSet(count)
                if (prev != count) {
                    log.info("[InstanceCount] 인스턴스 수 갱신: {} → {}", prev, count)
                }
            }
        } catch (e: Exception) {
            log.debug("[InstanceCount] K8s API 조회 실패. 현재 값 유지. count={}", activeCount.get())
        }
    }

    fun getCount(): Int = activeCount.get()

    private fun countReadyAddresses(response: Map<*, *>?): Int {
        if (response == null) return 0
        val subsets = response["subsets"] as? List<*> ?: return 0
        return subsets.sumOf { subset ->
            val map = subset as? Map<*, *> ?: return@sumOf 0
            val addresses = map["addresses"] as? List<*> ?: return@sumOf 0
            addresses.size
        }
    }

    private fun loadServiceAccountToken(): String {
        return try {
            java.io.File("/var/run/secrets/kubernetes.io/serviceaccount/token").readText()
        } catch (e: Exception) {
            ""
        }
    }
}
