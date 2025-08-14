package com.nearpick.domain.address.dto

data class CreateUserAddressRequest(
    val name: String,
    val receiverName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val addressDetail: String?,
    val province: String?,
    val district: String?,
    val neighborhood: String?,
    val street: String?,
    val buildingNumber: String?,
    val isDefault: Boolean = false
)
