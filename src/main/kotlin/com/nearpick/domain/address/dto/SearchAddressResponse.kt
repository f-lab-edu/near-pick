package com.nearpick.domain.address.dto

class SearchAddressResponse(
    val fullAddress: String?,

    val province: String?,
    val district: String?,
    val neighborhood: String?,
    val street: String?,
    val buildingNumber: String?
)
