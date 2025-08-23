package com.nearpick.domain.brand.dto

data class UpdateBrandRequest(
    val name: String,
    val description: String? = null,
    val fullAddress: String,
    val addressDetail: String? = null,
    val province: String? = null,
    val district: String? = null,
    val neighborhood: String? = null,
    val street: String? = null
)
