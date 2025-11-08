package com.nearpick.app.common.quartz.job

import com.nearpick.app.domain.stock.service.StockService
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
open class StockJob : QuartzJobBean() {

    @Autowired
    private lateinit var stockService: StockService

    override fun executeInternal(context: JobExecutionContext) {
        stockService.initializeDailyStock()
    }
}
