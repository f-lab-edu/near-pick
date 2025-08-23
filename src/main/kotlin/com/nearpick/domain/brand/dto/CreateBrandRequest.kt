package com.nearpick.domain.brand.dto

import com.nearpick.common.exception.InvalidBusinessRegistrationNumberException
import com.nearpick.common.validator.Validator

data class CreateBrandRequest(
    val name: String,
    val description: String? = null,
    val businessRegistrationNumber: String,
    val fullAddress: String,
    val addressDetail: String? = null,
    val province: String? = null,
    val district: String? = null,
    val neighborhood: String? = null,
    val street: String? = null
) {
    init {
        if (!Validator.isValidBusinessRegistrationNumber(businessRegistrationNumber)) {
            throw InvalidBusinessRegistrationNumberException(businessRegistrationNumber)
        }
    }
}
