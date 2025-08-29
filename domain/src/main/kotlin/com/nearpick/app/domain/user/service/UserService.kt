package com.nearpick.app.domain.user.service

import com.nearpick.app.domain.user.dto.CreateUserRequest
import com.nearpick.app.domain.user.dto.UpdateUserRequest
import com.nearpick.app.domain.user.dto.UserResponse

interface UserService {
    fun createUser(request: CreateUserRequest): UserResponse
    fun getUserById(id: String): UserResponse
    fun updateUser(id: String, request: UpdateUserRequest): UserResponse
    fun deleteUser(id: String)
}
