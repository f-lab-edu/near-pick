package com.nearpick.app.common.security

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.security.config.WebSecurityConfig
import com.nearpick.app.common.security.principal.CustomUserDetailsService
import com.nearpick.app.common.security.principal.UserPrincipal
import com.nearpick.app.domain.test.controller.TestController
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test

@WebMvcTest(TestController::class)
@Import(
    WebSecurityConfig::class,
    JwtAuthenticationFilter::class,
    JwtAuthenticationEntryPoint::class,
    JwtAccessDeniedHandler::class
)
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest(
    @Autowired val mockMvc: MockMvc
) {

    @MockitoBean
    lateinit var jwtTokenProviderAdapter: JwtTokenProviderAdapter

    @MockitoBean
    lateinit var userDetailsService: CustomUserDetailsService

    @Test
    fun `토큰이 없으면 SecurityContext에 인증 정보가 없어야 한다`() {
        mockMvc.get("/api/test")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `토큰이 유효하지 않으면 SecurityContext에 인증 정보가 없어야 한다`() {
        Mockito.`when`(jwtTokenProviderAdapter.validateToken("invalid.token")).thenReturn(false)

        mockMvc.get("/api/test") {
            header("Authorization", "Bearer invalid.token")
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `유효한 토큰이면 SecurityContext에 유효한 인증 정보가 설정 되어야 한다`() {
        val token = "valid.token"
        val userId = "user-123"
        val userDetails = UserPrincipal(userId, "test@example.com", "password", Role.USER, true)

        Mockito.`when`(jwtTokenProviderAdapter.validateToken(token)).thenReturn(true)
        Mockito.`when`(jwtTokenProviderAdapter.getUserId(token)).thenReturn(userId)
        Mockito.`when`(userDetailsService.loadUserByUsername(userId)).thenReturn(userDetails)

        mockMvc.get("/api/test") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            content { string("authenticated") }
        }
    }
}
