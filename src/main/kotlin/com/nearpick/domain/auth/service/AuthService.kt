package com.nearpick.domain.auth.service

import com.nearpick.common.exception.InvalidEmailException
import com.nearpick.common.exception.InvalidPasswordException
import com.nearpick.common.security.JwtTokenProvider
import com.nearpick.domain.auth.dto.LoginRequest
import com.nearpick.domain.auth.dto.LoginResponse
import com.nearpick.domain.user.repository.UserRepository
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
            ?: throw InvalidEmailException()

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidPasswordException()
        }

        val token = jwtTokenProvider.generateToken(user.id, user.role.name)
        return LoginResponse(accessToken = token)
    }
}
