package com.nearpick.app.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.stock.host}") private val stockHost: String,
    @Value("\${spring.data.redis.stock.port}") private val stockPort: Int,
    @Value("\${spring.data.redis.stock.timeout}") private val stockTimeout: Long
) {

    @Bean
    @Primary
    fun stockRedisConnectionFactory(): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration(stockHost, stockPort)
        val clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(stockTimeout))
            .build()
        return LettuceConnectionFactory(config, clientConfig)
    }

    @Bean
    @Primary
    fun redisTemplate(stockRedisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = stockRedisConnectionFactory

        val keySerializer = StringRedisSerializer()
        val valueSerializer = GenericJackson2JsonRedisSerializer()

        template.keySerializer = keySerializer
        template.hashKeySerializer = keySerializer
        template.valueSerializer = valueSerializer
        template.hashValueSerializer = valueSerializer

        return template
    }

    @Bean
    @Primary
    fun stringRedisTemplate(stockRedisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(stockRedisConnectionFactory)
    }
}
