package com.nearpick.app.domain.address.entity

import com.nearpick.app.domain.address.dto.CreateUserAddressRequest
import com.nearpick.app.domain.address.dto.UserAddressResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "user_address")
@EntityListeners(AuditingEntityListener::class)
class UserAddress(
    @Id
    val id: String,

    @Column(nullable = false)
    val userId: String,

    var name: String?,
    var receiverName: String?,
    var phoneNumber: String?,

    @Column(nullable = false)
    val fullAddress: String,
    var addressDetail: String?,

    val province: String?,
    val district: String?,
    val neighborhood: String?,
    val street: String?,
    val buildingNumber: String?,

    var isDefault: Boolean,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @CreatedBy
    val createdBy: String? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    val updatedBy: String? = null
) {

    companion object {
        fun createByUser(addressRequest: CreateUserAddressRequest, userId: String): UserAddress {
            return UserAddress(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = addressRequest.name,
                receiverName = addressRequest.receiverName,
                phoneNumber = addressRequest.phoneNumber,
                fullAddress = addressRequest.fullAddress,
                addressDetail = addressRequest.addressDetail,
                province = addressRequest.province,
                district = addressRequest.district,
                neighborhood = addressRequest.neighborhood,
                street = addressRequest.street,
                buildingNumber = addressRequest.buildingNumber,
                isDefault = addressRequest.isDefault ?: false
            )
        }

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
}
