package com.nearpick.app.domain.address.mapper

import com.nearpick.app.domain.address.entity.UserAddressEntity
import com.nearpick.app.domain.address.service.UserAddress
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserAddressMapper {
    fun toEntity(domain: UserAddress): UserAddressEntity {
        return UserAddressEntity(
            id = domain.id ?: UUID.randomUUID().toString(),
            userId = domain.userId,
            name = domain.name,
            receiverName = domain.receiverName,
            phoneNumber = domain.phoneNumber,
            fullAddress = domain.fullAddress,
            addressDetail = domain.addressDetail,
            province = domain.province,
            district = domain.district,
            neighborhood = domain.neighborhood,
            street = domain.street,
            buildingNumber = domain.buildingNumber,
            isDefault = domain.isDefault ?: false
        )
    }

    fun toDomain(entity: UserAddressEntity): UserAddress {
        return UserAddress(
            id = entity.id,
            userId = entity.userId,
            name = entity.name,
            receiverName = entity.receiverName,
            phoneNumber = entity.phoneNumber,
            fullAddress = entity.fullAddress,
            addressDetail = entity.addressDetail,
            province = entity.province,
            district = entity.district,
            neighborhood = entity.neighborhood,
            street = entity.street,
            buildingNumber = entity.buildingNumber,
            isDefault = entity.isDefault
        )
    }
}
