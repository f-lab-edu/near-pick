package com.nearpick.app.common.config

import com.nearpick.app.common.quartz.job.StockJob
import jakarta.annotation.PostConstruct
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import javax.sql.DataSource

@Configuration
class QuartzConfig(
    @Qualifier("quartzDataSource") private val quartzDataSource: DataSource
) {

    companion object {
        private const val DAILY_STOCK_JOB_CRON = "0 0 17 * * ?"

        private const val JOB_KEY_STOCK = "dailyStockJob"
        private const val TRIGGER_KEY_STOCK = "dailyStockTrigger"

        private const val SCHEDULER_NAME = "NearPickQuartzScheduler"
    }

    @Bean
    fun stockJobDetail(): JobDetail =
        JobBuilder.newJob(StockJob::class.java)
            .withIdentity(JOB_KEY_STOCK)
            .storeDurably()
            .build()

    @Bean
    fun stockTrigger(jobDetail: JobDetail): Trigger =
        TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(TRIGGER_KEY_STOCK)
            .withSchedule(
                CronScheduleBuilder.cronSchedule(DAILY_STOCK_JOB_CRON)
                    .withMisfireHandlingInstructionFireAndProceed()
            )
            .build()

    @Bean
    fun springBeanJobFactory(applicationContext: ApplicationContext): SpringBeanJobFactory {
        val jobFactory = object : SpringBeanJobFactory(), ApplicationContextAware {
            private lateinit var ctx: ApplicationContext
            override fun setApplicationContext(context: ApplicationContext) {
                this.ctx = context
            }

            override fun createJobInstance(bundle: TriggerFiredBundle): Any {
                val job = super.createJobInstance(bundle)
                ctx.autowireCapableBeanFactory.autowireBean(job)
                return job
            }
        }
        return jobFactory
    }

    @Bean
    fun schedulerFactoryBean(jobFactory: SpringBeanJobFactory): SchedulerFactoryBean =
        SchedulerFactoryBean().apply {
            setDataSource(quartzDataSource)
            setJobFactory(jobFactory)
            setWaitForJobsToCompleteOnShutdown(true)
            setOverwriteExistingJobs(true)
            setSchedulerName(SCHEDULER_NAME)
            setApplicationContextSchedulerContextKey("applicationContext")
        }

    @Bean
    fun schedulerInitializer(
        schedulerFactoryBean: SchedulerFactoryBean,
        stockJobDetail: JobDetail,
        stockTrigger: Trigger
    ): Any {
        return object {
            @PostConstruct
            fun registerJob() {
                val scheduler = schedulerFactoryBean.scheduler

                if (!scheduler.checkExists(stockJobDetail.key)) {
                    scheduler.scheduleJob(stockJobDetail, stockTrigger)
                }

                if (!scheduler.isStarted) {
                    scheduler.start()
                }
            }
        }
    }
}
