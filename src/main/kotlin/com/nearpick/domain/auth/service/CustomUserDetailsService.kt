package com.nearpick.domain.auth.service

import com.nearpick.common.exception.UserNotFoundException
import com.nearpick.domain.auth.dto.UserPrincipal
import com.nearpick.domain.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(id: String): UserDetails {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        return UserPrincipal.from(user)
    }
}
