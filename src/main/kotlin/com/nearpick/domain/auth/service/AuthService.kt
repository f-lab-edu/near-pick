package com.nearpick.domain.auth.service

import com.nearpick.common.exception.BaseException
import com.nearpick.common.security.JwtTokenProvider
import com.nearpick.domain.auth.dto.LoginRequest
import com.nearpick.domain.auth.dto.LoginResponse
import com.nearpick.domain.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw BaseException("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED)

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BaseException("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED)
        }

        val token = jwtTokenProvider.generateToken(user.id, user.role.name)
        return LoginResponse(accessToken = token)
    }
}
