package com.nearpick.app.domain.brand.mapper

import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.brand.service.Brand
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class BrandMapper (
    private val userRepository: UserRepository
) {
    fun toEntity(domain: Brand): BrandEntity {
        val ownerRef = userRepository.getReferenceById(domain.ownerUserId)

        return BrandEntity(
            id = domain.id ?: UUID.randomUUID().toString(),
            ownerUserEntity = ownerRef,
            name = domain.name,
            description = domain.description,
            businessRegistrationNumber = domain.businessRegistrationNumber,
            fullAddress = domain.fullAddress,
            addressDetail = domain.addressDetail,
            province = domain.province,
            district = domain.district,
            neighborhood = domain.neighborhood,
            street = domain.street
        )
    }

    fun toDomain(entity: BrandEntity): Brand {
        return Brand(
            id = entity.id,
            ownerUserId = entity.ownerUserEntity.id,
            name = entity.name,
            description = entity.description,
            businessRegistrationNumber = entity.businessRegistrationNumber,
            fullAddress = entity.fullAddress,
            addressDetail = entity.addressDetail,
            province = entity.province,
            district = entity.district,
            neighborhood = entity.neighborhood,
            street = entity.street
        )
    }
}
