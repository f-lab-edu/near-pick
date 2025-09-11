package com.nearpick.app.domain.address.service

import com.nearpick.app.common.exception.UserAddressNotFoundException
import com.nearpick.app.domain.address.dto.CreateUserAddressRequest
import com.nearpick.app.domain.address.dto.UpdateUserAddressRequest
import com.nearpick.app.domain.address.dto.UserAddressResponse
import com.nearpick.app.domain.address.repository.UserAddressRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional(readOnly = false)
open class UserAddressServiceImpl(
    private val userAddressRepository: UserAddressRepository
) : UserAddressService {


    override fun createUserAddress(request: CreateUserAddressRequest, userId: String): UserAddressResponse {

        val userAddress = UserAddress(
            userId = userId,
            name = request.name,
            receiverName = request.receiverName,
            phoneNumber = request.phoneNumber,
            fullAddress = request.fullAddress,
            addressDetail = request.addressDetail,
            province = request.province,
            district = request.district,
            neighborhood = request.neighborhood,
            street = request.street,
            buildingNumber = request.buildingNumber,
            isDefault = false
        )

        return UserAddress.toResponse(userAddressRepository.save(userAddress.toEntity()))
    }

    @Transactional(readOnly = true)
    override fun findAllUserAddressByUserId(userId: String): List<UserAddressResponse> {
        return userAddressRepository.findAllByUserId(userId).map { UserAddress.toResponse(it) }
    }

    override fun updateUserAddress(id: String, userId: String, request: UpdateUserAddressRequest): UserAddressResponse {
        val userAddressEntity =
            userAddressRepository.findByIdAndUserId(id, userId) ?: throw UserAddressNotFoundException(id, userId)
        val userAddress = UserAddress.from(userAddressEntity)

        userAddress.update(request)

        return UserAddress.toResponse(userAddressRepository.save(userAddress.toEntity()))
    }

    override fun deleteUserAddress(id: String, userId: String) {
        val address = userAddressRepository.findByIdAndUserId(id, userId) ?: throw UserAddressNotFoundException(
            id,
            userId
        )

        userAddressRepository.delete(address)
    }
}
