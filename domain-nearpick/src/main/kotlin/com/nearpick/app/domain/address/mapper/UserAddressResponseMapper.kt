package com.nearpick.app.domain.address.mapper

import com.nearpick.app.domain.address.dto.UserAddressResponse
import com.nearpick.app.domain.address.service.UserAddress
import org.springframework.stereotype.Component

@Component
class UserAddressResponseMapper {
    fun toResponse(address: UserAddress): UserAddressResponse {
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
