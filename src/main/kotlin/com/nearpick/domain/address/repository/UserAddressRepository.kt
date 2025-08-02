package com.nearpick.domain.address.repository

import com.nearpick.domain.address.entity.UserAddress
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserAddressRepository : JpaRepository<UserAddress, String> {
    fun findAllByUserId(userId: String): List<UserAddress>
    fun findByIdAndUserId(id: String, userId: String): Optional<UserAddress>
}
