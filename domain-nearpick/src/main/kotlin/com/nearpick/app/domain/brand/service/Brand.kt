package com.nearpick.app.domain.brand.service

import com.nearpick.app.common.exception.InvalidBusinessRegistrationNumberException
import com.nearpick.app.common.validator.Validator
import com.nearpick.app.domain.brand.dto.UpdateBrandRequest
import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.user.entity.User
import java.util.UUID

class Brand(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val businessRegistrationNumber: String,
    val fullAddress: String,
    val addressDetail: String? = null,
    val province: String? = null,
    val district: String? = null,
    val neighborhood: String? = null,
    val street: String? = null
) {
    init {
        validateBusinessRegistrationNumber(businessRegistrationNumber)
    }

    private fun validateBusinessRegistrationNumber(businessRegistrationNumber: String) {
        if (!Validator.isValidBusinessRegistrationNumber(businessRegistrationNumber)) {
            throw InvalidBusinessRegistrationNumberException(businessRegistrationNumber)
        }
    }

    fun toSellerEntity(user: User): BrandEntity {
        return BrandEntity(
            id = UUID.randomUUID().toString(),
            ownerUser = user,
            name = name,
            description = description,
            businessRegistrationNumber = businessRegistrationNumber,
            fullAddress = fullAddress,
            addressDetail = addressDetail,
            province = province,
            district = district,
            neighborhood = neighborhood,
            street = street
        )
    }

    fun update(request: UpdateBrandRequest) {
        validateBusinessRegistrationNumber(request.busineessNumber)
    }

    fun toEntity(): BrandEntity {
        TODO("Not yet implemented")
    }

    companion object {
        fun from(brandEntity: BrandEntity): Brand {
            return Brand(
                id = brandEntity.id,
                name = brandEntity.name,
                description = brandEntity.description,
                businessRegistrationNumber = brandEntity.businessRegistrationNumber,
                fullAddress = brandEntity.fullAddress,
                addressDetail = brandEntity.addressDetail,
                province = brandEntity.province,
                district = brandEntity.district,
                neighborhood = brandEntity.neighborhood,
                street = brandEntity.street
            )
        }
    }
}
