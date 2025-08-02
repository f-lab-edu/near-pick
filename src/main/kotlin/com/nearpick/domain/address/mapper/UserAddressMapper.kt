package com.nearpick.domain.address.mapper

import com.nearpick.domain.address.dto.CreateUserAddressRequest
import com.nearpick.domain.address.dto.SearchAddressDto
import com.nearpick.domain.address.dto.SearchAddressResponse
import com.nearpick.domain.address.dto.UserAddressResponse
import com.nearpick.domain.address.entity.UserAddress

fun SearchAddressDto.Document.toDto(): SearchAddressResponse = SearchAddressResponse(
    fullAddress = this.address?.address_name ?: this.road_address?.address_name,
    province = this.address?.region_1depth_name,
    district = this.address?.region_2depth_name,
    neighborhood = address?.region_3depth_name,
    street = this.road_address?.road_name,
    buildingNumber = this.road_address?.main_building_no
)

fun List<SearchAddressDto.Document>.toDtoList(): List<SearchAddressResponse> =
    this.map { it.toDto() }

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
