package com.nearpick.app.domain.user.mapper

import com.nearpick.app.domain.user.entity.UserEntity
import com.nearpick.app.domain.user.service.User
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserMapper {
    fun toEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id ?: UUID.randomUUID().toString(),
            email = domain.email,
            nickname = domain.nickname,
            password = domain.password,
            profileImageUrl = domain.profileImageUrl,
            phoneNumber = domain.phoneNumber,
            role = domain.role,
            accountHolderName = domain.accountHolderName,
            bankName = domain.bankName,
            accountNumber = domain.accountNumber,
            isActive = domain.isActive
        )
    }

    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            email = entity.email,
            nickname = entity.nickname,
            password = entity.password,
            profileImageUrl = entity.profileImageUrl,
            phoneNumber = entity.phoneNumber,
            role = entity.role,
            accountHolderName = entity.accountHolderName,
            bankName = entity.bankName,
            accountNumber = entity.accountNumber,
            isActive = entity.isActive
        )
    }
}
