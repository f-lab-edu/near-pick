package com.nearpick.app.common.config.datasource

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class QuartzDataSourceConfig {

    @Bean
    @Qualifier("quartzDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.quartz")
    fun quartzDataSource(): DataSource =
        DataSourceBuilder.create().build()
}
