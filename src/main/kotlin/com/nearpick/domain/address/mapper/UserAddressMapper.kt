package com.nearpick.domain.address.mapper

import com.nearpick.domain.address.dto.CreateUserAddressRequest
import com.nearpick.domain.address.dto.SearchAddressDto
import com.nearpick.domain.address.dto.SearchAddressResponse
import com.nearpick.domain.address.dto.UserAddressResponse
import com.nearpick.domain.address.entity.UserAddress

fun List<SearchAddressDto.Document>.toDtoList(): List<SearchAddressResponse> =
    this.map {
        SearchAddressResponse(
            fullAddress = it.address?.address_name ?: it.road_address?.address_name,
            province = it.address?.region_1depth_name,
            district = it.address?.region_2depth_name,
            neighborhood = it.address?.region_3depth_name,
            street = it.road_address?.road_name,
            buildingNumber = it.road_address?.main_building_no
        )
    }

fun CreateUserAddressRequest.toEntity(userId: String): UserAddress = UserAddress(
    userId = userId,
    name = this.name,
    receiverName = this.receiverName,
    phoneNumber = this.phoneNumber,
    fullAddress = this.fullAddress,
    addressDetail = this.addressDetail,
    province = this.province,
    district = this.district,
    neighborhood = this.neighborhood,
    street = this.street,
    buildingNumber = this.buildingNumber,
    isDefault = this.isDefault
)

fun UserAddress.toResponse(): UserAddressResponse = UserAddressResponse(
    id = id,
    name = name,
    receiverName = receiverName,
    phoneNumber = phoneNumber,
    fullAddress = fullAddress,
    addressDetail = addressDetail,
    isDefault = isDefault
)
