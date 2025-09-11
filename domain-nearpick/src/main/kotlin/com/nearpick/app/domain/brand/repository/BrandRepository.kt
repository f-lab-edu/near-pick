package com.nearpick.app.domain.brand.repository

import com.nearpick.app.domain.brand.entity.BrandEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository : JpaRepository<BrandEntity, String> {
    fun findAllByOwnerUserEntityId(userId: String): List<BrandEntity>
    fun findByIdAndOwnerUserEntityId(id: String, userId: String): BrandEntity?

    fun existsByBusinessRegistrationNumber(businessRegistrationNumber: String): Boolean
}
