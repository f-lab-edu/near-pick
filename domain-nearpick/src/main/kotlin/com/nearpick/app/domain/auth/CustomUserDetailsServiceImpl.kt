package com.nearpick.app.domain.auth

import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.common.user.UserPrincipal
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class CustomUserDetailsServiceImpl(
    private val userRepository: UserRepository
) : CustomUserDetailsService {

    override fun loadUserByUsername(id: String): UserDetails {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        return UserPrincipal(
            userId = user.id,
            email = user.email,
            userPassword = user.password,
            role = user.role,
            isActive = user.isActive
        )
    }
}
