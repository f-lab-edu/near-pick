package com.nearpick.app.common.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import java.time.Duration

@Configuration
class RateLimitRedisConfig(
    @Value("\${spring.data.redis.ratelimit.host}") private val host: String,
    @Value("\${spring.data.redis.ratelimit.port}") private val port: Int,
    @Value("\${spring.data.redis.ratelimit.timeout}") private val timeout: Long
) {

    @Bean
    fun rateLimitRedisConnectionFactory(): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port)
        val clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(timeout))
            .build()
        return LettuceConnectionFactory(config, clientConfig)
    }

    @Bean
    fun rateLimitRedisTemplate(
        @Qualifier("rateLimitRedisConnectionFactory") connectionFactory: RedisConnectionFactory
    ): StringRedisTemplate {
        return StringRedisTemplate(connectionFactory)
    }

    @Bean
    fun rateLimitRedisMessageListenerContainer(
        @Qualifier("rateLimitRedisConnectionFactory") connectionFactory: RedisConnectionFactory
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        return container
    }
}
