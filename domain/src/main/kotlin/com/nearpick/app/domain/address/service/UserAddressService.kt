package com.nearpick.app.domain.address.service

import com.nearpick.app.domain.address.dto.CreateUserAddressRequest
import com.nearpick.app.domain.address.dto.UpdateUserAddressRequest
import com.nearpick.app.domain.address.dto.UserAddressResponse

interface UserAddressService {
    fun createUserAddress(request: CreateUserAddressRequest, userId: String): UserAddressResponse
    fun findAllUserAddressByUserId(userId: String): List<UserAddressResponse>
    fun updateUserAddress(id: String, userId: String, req: UpdateUserAddressRequest): UserAddressResponse
    fun deleteUserAddress(id: String, userId: String)
}
