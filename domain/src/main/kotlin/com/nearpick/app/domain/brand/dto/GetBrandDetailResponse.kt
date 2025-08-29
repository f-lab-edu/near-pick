package com.nearpick.app.domain.brand.dto

import com.nearpick.app.domain.user.dto.UserResponse

data class GetBrandDetailResponse(
    val id: String,
    val ownerUser: UserResponse,
    val name: String,
    val description: String? = null,
    val businessRegistrationNumber: String,
    val fullAddress: String,
    val addressDetail: String? = null,
    val province: String? = null,
    val district: String? = null,
    val neighborhood: String? = null,
    val street: String? = null
)
