package com.nearpick.app.domain.address.repository

import com.nearpick.app.domain.address.entity.UserAddressEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserAddressRepository : JpaRepository<UserAddressEntity, String> {
    fun findAllByUserId(userId: String): List<UserAddressEntity>
    fun findByIdAndUserId(id: String, userId: String): UserAddressEntity?
}
