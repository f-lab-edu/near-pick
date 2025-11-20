package com.nearpick.app.domain.user.mapper

import com.nearpick.app.domain.user.dto.UserPrincipalResponse
import com.nearpick.app.domain.user.dto.UserResponse
import com.nearpick.app.domain.user.service.User
import org.springframework.stereotype.Component

@Component
class UserResponseMapper {
    fun toResponse(domain: User): UserResponse =
        UserResponse(
            id = domain.id,
            email = domain.email,
            nickname = domain.nickname,
            profileImageUrl = domain.profileImageUrl,
            phoneNumber = domain.phoneNumber,
            role = domain.role.name,
            accountHolderName = domain.accountHolderName,
            bankName = domain.bankName,
            accountNumber = domain.accountNumber,
            isActive = domain.isActive
        )

    fun toPrincipalResponse(domain: User): UserPrincipalResponse =
        UserPrincipalResponse(
            id = domain.id?: "anonymous",
            email = domain.email,
            password = domain.password,
            role = domain.role,
            isActive = domain.isActive
        )
}
