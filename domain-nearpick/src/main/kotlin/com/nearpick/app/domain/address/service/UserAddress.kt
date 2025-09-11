package com.nearpick.app.domain.address.service

import com.nearpick.app.common.exception.InvalidPhoneNumberException
import com.nearpick.app.common.validator.Validator
import com.nearpick.app.domain.address.dto.CreateUserAddressRequest
import com.nearpick.app.domain.address.dto.UpdateUserAddressRequest
import com.nearpick.app.domain.address.dto.UserAddressResponse
import com.nearpick.app.domain.address.entity.UserAddressEntity
import java.util.*


class UserAddress(
    val id: String?= null,
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
    var isDefault: Boolean? = false,
) {
    init {
        val num = phoneNumber
        if (!num.isNullOrBlank() && !Validator.isValidPhoneNumber(num)) {
            throw InvalidPhoneNumberException(num)
        }
    }

    fun update(request: UpdateUserAddressRequest) {
        this.name = request.name ?: this.name
        this.receiverName = request.receiverName ?: this.receiverName
        this.phoneNumber = request.phoneNumber ?: this.phoneNumber
        this.addressDetail = request.addressDetail ?: this.addressDetail
        this.isDefault = request.isDefault ?: this.isDefault
    }

    fun toEntity(): UserAddressEntity {
        return UserAddressEntity(
            id = id?: UUID.randomUUID().toString(),
            userId = userId,
            name = name,
            receiverName = receiverName,
            phoneNumber = phoneNumber,
            fullAddress = fullAddress,
            addressDetail = addressDetail,
            province = province,
            district = district,
            neighborhood = neighborhood,
            street = street,
            buildingNumber = buildingNumber,
            isDefault = isDefault ?: false
        )
    }

    companion object {
        fun from(userAddressEntity: UserAddressEntity): UserAddress {
            return UserAddress(
                id = userAddressEntity.id,
                userId = userAddressEntity.userId,
                name = userAddressEntity.name,
                receiverName = userAddressEntity.receiverName,
                phoneNumber = userAddressEntity.phoneNumber,
                fullAddress = userAddressEntity.fullAddress,
                addressDetail = userAddressEntity.addressDetail,
                province = userAddressEntity.province,
                district = userAddressEntity.district,
                neighborhood = userAddressEntity.neighborhood,
                street = userAddressEntity.street,
                buildingNumber = userAddressEntity.buildingNumber,
                isDefault = userAddressEntity.isDefault
            )
        }

        fun toResponse(address: UserAddressEntity): UserAddressResponse {
            return UserAddressResponse(
                id = address.id,
                name = address.name,
                receiverName = address.receiverName,
                phoneNumber = address.phoneNumber,
                fullAddress = address.fullAddress,
                addressDetail = address.addressDetail,
                isDefault = address.isDefault
            )
        }
    }
}

