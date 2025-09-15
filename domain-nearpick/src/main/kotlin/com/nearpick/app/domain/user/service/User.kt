package com.nearpick.app.domain.user.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.InvalidPhoneNumberException
import com.nearpick.app.common.validator.Validator
import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.brand.service.Brand
import com.nearpick.app.domain.user.dto.UpdateUserRequest
import com.nearpick.app.domain.user.dto.UserPrincipalResponse
import com.nearpick.app.domain.user.dto.UserResponse
import com.nearpick.app.domain.user.entity.UserEntity
import java.util.*

class User(
    val id: String?= null,
    var email: String,
    var nickname: String,
    var password: String,
    var profileImageUrl: String? = null,
    var phoneNumber: String? = null,
    var accountHolderName: String? = null,
    var bankName: String? = null,
    var accountNumber: String? = null,
    var role: Role = Role.USER,
    var isActive: Boolean = true
) {
    init {
        val num: String? = phoneNumber
        if (!num.isNullOrBlank() && !Validator.isValidPhoneNumber(num)) {
            throw InvalidPhoneNumberException(num)
        }
    }

    fun toEntity(): UserEntity {
        return UserEntity(
            id = id?: UUID.randomUUID().toString(),
            email = email,
            nickname = nickname,
            password = password,
            profileImageUrl = profileImageUrl,
            phoneNumber = phoneNumber,
            role = role,
            accountHolderName = accountHolderName,
            bankName = bankName,
            accountNumber = accountNumber,
            isActive = isActive
        )
    }

    fun update(request: UpdateUserRequest) {
        val num: String? = request.phoneNumber
        if (!num.isNullOrBlank() && !Validator.isValidPhoneNumber(num)) {
            throw InvalidPhoneNumberException(num)
        }
        this.email = request.email ?: this.email
        this.nickname = request.nickname ?: this.nickname
        this.profileImageUrl = request.profileImageUrl ?: this.profileImageUrl
        this.phoneNumber = request.phoneNumber ?: this.phoneNumber
        this.accountHolderName = request.accountHolderName ?: this.accountHolderName
        this.bankName = request.bankName ?: this.bankName
        this.accountNumber = request.accountNumber ?: this.accountNumber
    }
    companion object {
        fun from(userEntity: UserEntity): User {
            return User(
                id = userEntity.id,
                email = userEntity.email,
                nickname = userEntity.nickname,
                password = userEntity.password,
                profileImageUrl = userEntity.profileImageUrl,
                phoneNumber = userEntity.phoneNumber,
                role = userEntity.role,
                accountHolderName = userEntity.accountHolderName,
                bankName = userEntity.bankName,
                accountNumber = userEntity.accountNumber,
                isActive = userEntity.isActive
            )
        }

        fun toResponse(userEntity: UserEntity): UserResponse =
            UserResponse(
                id = userEntity.id,
                email = userEntity.email,
                nickname = userEntity.nickname,
                profileImageUrl = userEntity.profileImageUrl,
                phoneNumber = userEntity.phoneNumber,
                role = userEntity.role.name,
                accountHolderName = userEntity.accountHolderName,
                bankName = userEntity.bankName,
                accountNumber = userEntity.accountNumber,
                isActive = userEntity.isActive
            )

        fun toPrincipalResponse(userEntity: UserEntity): UserPrincipalResponse =
            UserPrincipalResponse(
                id = userEntity.id,
                email = userEntity.email,
                password = userEntity.password,
                role = userEntity.role,
                isActive = userEntity.isActive
            )
    }
}

