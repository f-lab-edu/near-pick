package com.nearpick.app.domain.brand.service

import com.nearpick.app.common.exception.InvalidBusinessRegistrationNumberException
import com.nearpick.app.common.validator.Validator
import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.brand.dto.UpdateBrandRequest
import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.user.entity.UserEntity
import com.nearpick.app.domain.user.service.User
import java.util.UUID

class Brand(
    val id: String? = null,
    val ownerUserEntity: UserEntity,
    var name: String,
    var description: String? = null,
    val businessRegistrationNumber: String,
    var fullAddress: String,
    var addressDetail: String? = null,
    var province: String? = null,
    var district: String? = null,
    var neighborhood: String? = null,
    var street: String? = null
) {
    init {
        validateBusinessRegistrationNumber(businessRegistrationNumber)
    }

    private fun validateBusinessRegistrationNumber(businessRegistrationNumber: String) {
        if (!Validator.isValidBusinessRegistrationNumber(businessRegistrationNumber)) {
            throw InvalidBusinessRegistrationNumberException(businessRegistrationNumber)
        }
    }

    fun update(request: UpdateBrandRequest) {
        this.name = request.name ?: this.name
        this.description = request.description ?: this.description
        this.fullAddress = request.fullAddress ?: this.fullAddress
        this.addressDetail = request.addressDetail ?: this.addressDetail
        this.province = request.province ?: this.province
        this.district = request.district ?: this.district
        this.neighborhood = request.neighborhood ?: this.neighborhood
        this.street = request.street ?: this.street
    }

    fun toEntity(): BrandEntity {
        return BrandEntity(
            id = id?: UUID.randomUUID().toString(),
            ownerUserEntity = ownerUserEntity,
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

    companion object {
        fun from(brandEntity: BrandEntity): Brand {
            return Brand(
                id = brandEntity.id,
                ownerUserEntity = brandEntity.ownerUserEntity,
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

        fun toResponse(brandEntity: BrandEntity): BrandResponse =
            BrandResponse(
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

        fun toDetailResponse(brandEntity: BrandEntity): GetBrandDetailResponse =
            GetBrandDetailResponse(
                id = brandEntity.id,
                ownerUser = User.toResponse(brandEntity.ownerUserEntity),
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
