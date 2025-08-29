package com.nearpick.app.domain.address.service

import com.nearpick.app.common.exception.UserAddressNotFoundException
import com.nearpick.app.domain.address.dto.CreateUserAddressRequest
import com.nearpick.app.domain.address.dto.UpdateUserAddressRequest
import com.nearpick.app.domain.address.dto.UserAddressResponse
import com.nearpick.app.domain.address.entity.UserAddress
import com.nearpick.app.domain.address.repository.UserAddressRepository
import org.springframework.stereotype.Service

@Service
class UserAddressServiceImpl(
    private val userAddressRepository: UserAddressRepository
) : UserAddressService {
    override fun createUserAddress(request: CreateUserAddressRequest, userId: String): UserAddressResponse {
        val entity = UserAddress.createByUser(request, userId)
        return UserAddress.toResponse(userAddressRepository.save(entity))
    }

    override fun findAllUserAddressByUserId(userId: String): List<UserAddressResponse> {
        return userAddressRepository.findAllByUserId(userId).map { UserAddress.toResponse(it) }
    }

    override fun updateUserAddress(id: String, userId: String, req: UpdateUserAddressRequest): UserAddressResponse {
        val address =
            userAddressRepository.findByIdAndUserId(id, userId) ?: throw UserAddressNotFoundException(id, userId)

        val updated = address.apply {
            name = req.name
            receiverName = req.receiverName
            phoneNumber = req.phoneNumber
            addressDetail = req.addressDetail
            isDefault = req.isDefault
        }

        return UserAddress.toResponse(userAddressRepository.save(updated))
    }

    override fun deleteUserAddress(id: String, userId: String) {
        val address = userAddressRepository.findByIdAndUserId(id, userId) ?: throw UserAddressNotFoundException(
            id,
            userId
        )

        userAddressRepository.delete(address)
    }
}
