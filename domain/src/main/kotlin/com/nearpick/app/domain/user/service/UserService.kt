package com.nearpick.app.domain.user.service

import com.nearpick.app.domain.user.dto.CreateUserRequest
import com.nearpick.app.domain.user.dto.UpdateUserRequest
import com.nearpick.app.domain.user.dto.UserPrincipalResponse
import com.nearpick.app.domain.user.dto.UserResponse

interface UserService {
    fun loadUserByUsername(id: String): UserPrincipalResponse
    fun createUser(request: CreateUserRequest): UserResponse
    fun checkEmail(email: String)
    fun checkNickname(nickname: String)
    fun getUserById(id: String): UserResponse
    fun updateUser(id: String, request: UpdateUserRequest): UserResponse
    fun deleteUser(id: String)
}
