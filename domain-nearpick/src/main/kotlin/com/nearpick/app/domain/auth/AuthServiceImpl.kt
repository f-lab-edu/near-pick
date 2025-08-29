package com.nearpick.app.domain.auth

import com.nearpick.app.common.exception.InvalidEmailException
import com.nearpick.app.common.exception.InvalidPasswordException
import com.nearpick.app.common.security.JwtTokenProvider
import com.nearpick.app.domain.auth.dto.LoginRequest
import com.nearpick.app.domain.auth.dto.LoginResponse
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) : AuthService {
    override fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidEmailException()

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidPasswordException()
        }

        val token = jwtTokenProvider.generateToken(user.id, user.role.name)
        return LoginResponse(accessToken = token)
    }
}
