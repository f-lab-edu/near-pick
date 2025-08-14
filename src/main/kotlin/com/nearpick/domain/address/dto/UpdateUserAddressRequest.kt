package com.nearpick.domain.address.dto

data class UpdateUserAddressRequest(
    val name: String,
    val receiverName: String,
    val phoneNumber: String,
    val addressDetail: String?,
    val isDefault: Boolean = false
)
