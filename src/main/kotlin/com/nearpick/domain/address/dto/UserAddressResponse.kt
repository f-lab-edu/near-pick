package com.nearpick.domain.address.dto

data class UserAddressResponse(
    val id: String,
    val name: String?,
    val receiverName: String?,
    val phoneNumber: String?,
    val fullAddress: String,
    val addressDetail: String?,
    val isDefault: Boolean
)
