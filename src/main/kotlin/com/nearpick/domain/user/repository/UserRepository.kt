package com.nearpick.domain.user.repository

import com.nearpick.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
    fun existsByEmail(email: String): Boolean
}
