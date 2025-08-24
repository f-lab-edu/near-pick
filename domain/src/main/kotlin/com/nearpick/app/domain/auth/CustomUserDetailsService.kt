package com.nearpick.app.domain.auth

import org.springframework.security.core.userdetails.UserDetailsService

interface CustomUserDetailsService : UserDetailsService {
    // Inherits loadUserByUsername(username: String): UserDetails from UserDetailsService
}
