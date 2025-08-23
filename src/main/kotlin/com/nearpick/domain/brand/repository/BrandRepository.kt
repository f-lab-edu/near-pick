package com.nearpick.domain.brand.repository

import com.nearpick.domain.brand.entity.Brand
import com.nearpick.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository : JpaRepository<Brand, String> {
    fun findAllByOwnerUser(user: User): List<Brand>
    fun findByIdAndOwnerUser(id: String, user: User): Brand?

    fun existsByBusinessRegistrationNumber(businessRegistrationNumber: String): Boolean
}
