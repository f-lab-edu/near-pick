package com.nearpick.app.common.security.principal

import com.nearpick.app.domain.user.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    val userService: UserService
) : UserDetailsService {
    override fun loadUserByUsername(id: String): UserDetails {
        val user = userService.loadUserByUsername(id)

        return UserPrincipal(
            userId = user.id,
            email = user.email,
            userPassword = user.password,
            role = user.role,
            isActive = user.isActive
        )
    }
}
