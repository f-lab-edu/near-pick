package com.nearpick.app.domain.user.repository

import com.nearpick.app.domain.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, String> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): UserEntity?
    fun existsByNickname(nickname: String): Boolean
}
