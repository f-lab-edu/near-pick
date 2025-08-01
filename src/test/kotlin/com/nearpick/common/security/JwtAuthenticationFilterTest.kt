package com.nearpick.common.security

import com.nearpick.domain.auth.service.CustomUserDetailsService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User

class JwtAuthenticationFilterTest : StringSpec({

    val jwtTokenProvider = mockk<JwtTokenProvider>()
    val userDetailsService = mockk<CustomUserDetailsService>()
    val filter = JwtAuthenticationFilterForTest(jwtTokenProvider, userDetailsService)
    val request = mockk<HttpServletRequest>(relaxed = true)
    val response = mockk<HttpServletResponse>(relaxed = true)
    val chain = mockk<FilterChain>(relaxed = true)

    beforeTest {
        SecurityContextHolder.clearContext()
    }

    "토큰이 없으면 SecurityContext에 인증 정보가 없어야 한다" {
        every { request.getHeader("Authorization") } returns null

        filter.testDoFilterInternal(request, response, chain)

        SecurityContextHolder.getContext().authentication.shouldBeNull()
    }

    "토큰이 유효하지 않으면 SecurityContext에 인증 정보가 없어야 한다" {
        every { request.getHeader("Authorization") } returns "Bearer invalid.token"
        every { jwtTokenProvider.validateToken("invalid.token") } returns false

        filter.testDoFilterInternal(request, response, chain)

        SecurityContextHolder.getContext().authentication.shouldBeNull()
    }

    "유효한 토큰이면 SecurityContext에 유효한 인증 정보가 설정 되어야 한다" {
        val token = "Bearer valid.token"
        val userId = "user-123"
        val userDetails = User("user-123", "", listOf()) // empty authorities

        every { request.getHeader("Authorization") } returns token
        every { jwtTokenProvider.validateToken("valid.token") } returns true
        every { jwtTokenProvider.getUserId("valid.token") } returns userId
        every { userDetailsService.loadUserByUsername(userId) } returns userDetails
        every { request.remoteAddr } returns "127.0.0.1"
        every { request.session } returns null

        filter.testDoFilterInternal(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        auth.shouldNotBe(null)
        auth!!.principal shouldNotBe null
        auth is UsernamePasswordAuthenticationToken
    }
})
