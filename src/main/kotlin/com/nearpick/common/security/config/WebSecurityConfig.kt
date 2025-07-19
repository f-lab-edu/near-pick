package com.nearpick.common.security.config

import com.nearpick.common.security.JwtAccessDeniedHandler
import com.nearpick.common.security.JwtAuthenticationEntryPoint
import com.nearpick.common.security.JwtAuthenticationFilter
import com.nearpick.common.security.constant.SecurityPermitUrls
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*SecurityPermitUrls.ALL_PUBLIC_URLS).permitAll()
                    .requestMatchers(*SecurityPermitUrls.USER_URLS).hasRole("USER")
                    .requestMatchers(*SecurityPermitUrls.SELLER_URLS).hasRole("SELLER")
                    .requestMatchers(*SecurityPermitUrls.ADMIN_URLS).hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                it.accessDeniedHandler(jwtAccessDeniedHandler)
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
