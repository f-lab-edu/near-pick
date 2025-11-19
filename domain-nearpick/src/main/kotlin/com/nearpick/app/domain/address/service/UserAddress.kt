package com.nearpick.app.domain.address.service

import com.nearpick.app.common.exception.InvalidPhoneNumberException
import com.nearpick.app.common.validator.Validator


class UserAddress(
    val id: String? = null,
    val userId: String,
    var name: String? = null,
    var receiverName: String? = null,
    var phoneNumber: String? = null,
    val fullAddress: String,
    var addressDetail: String? = null,
    val province: String? = null,
    val district: String? = null,
    val neighborhood: String? = null,
    val street: String? = null,
    val buildingNumber: String? = null,
    var isDefault: Boolean = false,
) {
    init {
        val num = phoneNumber
        if (!num.isNullOrBlank() && !Validator.isValidPhoneNumber(num)) {
            throw InvalidPhoneNumberException(num)
        }
    }

    fun update(
        name: String?,
        receiverName: String?,
        phoneNumber: String?,
        addressDetail: String?,
        isDefault: Boolean?
    ) {
        this.name = name ?: this.name
        this.receiverName = receiverName ?: this.receiverName
        this.phoneNumber = phoneNumber ?: this.phoneNumber
        this.addressDetail = addressDetail ?: this.addressDetail
        this.isDefault = isDefault ?: this.isDefault
    }
}

