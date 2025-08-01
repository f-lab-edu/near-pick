package com.nearpick.common.security

import com.nearpick.domain.auth.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

internal class JwtAuthenticationFilterForTest(
    jwtTokenProvider: JwtTokenProvider,
    userDetailsService: CustomUserDetailsService
) : JwtAuthenticationFilter(jwtTokenProvider, userDetailsService) {

    fun testDoFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        super.doFilterInternal(request, response, filterChain)
    }
}
