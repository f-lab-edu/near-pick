package com.nearpick.app.domain.brand.dto

import com.nearpick.app.common.exception.InvalidBusinessRegistrationNumberException
import com.nearpick.app.common.validator.Validator

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
