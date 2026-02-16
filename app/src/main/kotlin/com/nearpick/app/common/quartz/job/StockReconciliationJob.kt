package com.nearpick.app.common.quartz.job

import com.nearpick.app.domain.stock.service.StockService
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
open class StockReconciliationJob : QuartzJobBean() {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var stockService: StockService

    override fun executeInternal(context: JobExecutionContext) {
        log.info("[Reconciliation] 재고 정합성 보정 시작")
        val result = stockService.reconcileAll()
        log.info(
            "[Reconciliation] 보정 완료. checked={}, corrected={}",
            result.checked, result.corrected
        )
    }
}
