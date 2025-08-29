package com.nearpick.app.domain.brand.dto

data class UpdateBrandRequest(
    val name: String? = null,
    val description: String? = null,
    val fullAddress: String? = null,
    val addressDetail: String? = null,
    val province: String? = null,
    val district: String? = null,
    val neighborhood: String? = null,
    val street: String? = null
)
