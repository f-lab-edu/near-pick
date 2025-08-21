package com.nearpick.domain.user.mapper

import com.nearpick.common.constant.Role
import com.nearpick.domain.user.dto.CreateUserRequest
import com.nearpick.domain.user.dto.UserResponse
import com.nearpick.domain.user.entity.User
import java.util.UUID

fun User.toResponse(): UserResponse = UserResponse(
    id = this.id,
    email = this.email,
    nickname = this.nickname,
    profileImageUrl = this.profileImageUrl,
    phoneNumber = this.phoneNumber,
    role = this.role.name,
    isActive = this.isActive
)

fun CreateUserRequest.toEntity(role: Role, encodedPassword: String): User = User(
    id = UUID.randomUUID().toString(),
    email = this.email,
    nickname = this.nickname,
    password = encodedPassword,
    profileImageUrl = this.profileImageUrl,
    phoneNumber = this.phoneNumber,
    role = role
)
