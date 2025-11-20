package com.nearpick.app.domain.brand.service

import com.nearpick.app.common.exception.InvalidBusinessRegistrationNumberException
import com.nearpick.app.common.validator.Validator

class Brand(
    val id: String? = null,
    val ownerUserId: String,
    var name: String,
    var description: String? = null,
    val businessRegistrationNumber: String,
    var fullAddress: String,
    var addressDetail: String? = null,
    var province: String? = null,
    var district: String? = null,
    var neighborhood: String? = null,
    var street: String? = null
) {
    init {
        validateBusinessRegistrationNumber(businessRegistrationNumber)
    }

    private fun validateBusinessRegistrationNumber(businessRegistrationNumber: String) {
        if (!Validator.isValidBusinessRegistrationNumber(businessRegistrationNumber)) {
            throw InvalidBusinessRegistrationNumberException(businessRegistrationNumber)
        }
    }

    fun update(
        name: String?,
        description: String?,
        fullAddress: String?,
        addressDetail: String?,
        province: String?,
        district: String?,
        neighborhood: String?,
        street: String?
    ) {
        this.name = name ?: this.name
        this.description = description ?: this.description
        this.fullAddress = fullAddress ?: this.fullAddress
        this.addressDetail = addressDetail ?: this.addressDetail
        this.province = province ?: this.province
        this.district = district ?: this.district
        this.neighborhood = neighborhood ?: this.neighborhood
        this.street = street ?: this.street
    }
}
