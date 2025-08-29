package com.nearpick.app.domain.brand.repository

import com.nearpick.app.domain.brand.entity.Brand
import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository : JpaRepository<Brand, String> {
    fun findAllByOwnerUserId(userId: String): List<Brand>
    fun findByIdAndOwnerUserId(id: String, userId: String): Brand?

    fun existsByBusinessRegistrationNumber(businessRegistrationNumber: String): Boolean
}
