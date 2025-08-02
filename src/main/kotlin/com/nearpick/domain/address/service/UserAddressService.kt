package com.nearpick.domain.address.service

import com.nearpick.common.exception.UserAddressNotFoundException
import com.nearpick.domain.address.dto.CreateUserAddressRequest
import com.nearpick.domain.address.dto.UpdateUserAddressRequest
import com.nearpick.domain.address.dto.UserAddressResponse
import com.nearpick.domain.address.mapper.toEntity
import com.nearpick.domain.address.mapper.toResponse
import com.nearpick.domain.address.repository.UserAddressRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class UserAddressService(
    private val userAddressRepository: UserAddressRepository
) {
    fun createUserAddress(request: CreateUserAddressRequest, userId: String): UserAddressResponse {
        val entity = request.toEntity(userId)
        return userAddressRepository.save(entity).toResponse()
    }

    fun findAllUserAddressByUserId(userId: String): List<UserAddressResponse> {
        return userAddressRepository.findAllByUserId(userId).map { it.toResponse() }
    }

    fun updateUserAddress(id: String, userId: String, req: UpdateUserAddressRequest): UserAddressResponse {
        val address = userAddressRepository.findByIdAndUserId(id, userId).getOrElse {
            throw UserAddressNotFoundException(
                id,
                userId
            )
        }

        val updated = address.apply {
            name = req.name
            receiverName = req.receiverName
            phoneNumber = req.phoneNumber
            addressDetail = req.addressDetail
            isDefault = req.isDefault
        }

        return userAddressRepository.save(updated).toResponse()
    }

    fun deleteUserAddress(id: String, userId: String) {
        val address = userAddressRepository.findByIdAndUserId(id, userId).getOrElse {
            throw UserAddressNotFoundException(
                id,
                userId
            )
        }

        userAddressRepository.delete(address)
    }
}
