package com.nearpick.app.domain.brand.mapper

import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.brand.service.Brand
import com.nearpick.app.domain.user.dto.UserResponse
import org.springframework.stereotype.Component

@Component
class BrandResponseMapper {
    fun toResponse(domain: Brand): BrandResponse =
        BrandResponse(
            id = domain.id,
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

    fun toDetailResponse(
        domain: Brand,
        owner: UserResponse
    ): GetBrandDetailResponse =
        GetBrandDetailResponse(
            id = domain.id,
            ownerUser = owner,
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
