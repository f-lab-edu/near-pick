package com.nearpick.app.domain.brand.service

import com.nearpick.app.common.exception.BrandAlreadyExistsException
import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.CreateBrandRequest
import com.nearpick.app.domain.brand.entity.Brand
import com.nearpick.app.domain.brand.repository.BrandRepository
import com.nearpick.app.common.exception.BrandNotFoundException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.brand.dto.UpdateBrandRequest
import com.nearpick.app.domain.user.entity.User
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class BrandServiceImpl(
    private val userRepository: UserRepository,
    private val brandRepository: BrandRepository
) : BrandService {
    override fun createBrand(request: CreateBrandRequest, userId: String): BrandResponse {
        if (brandRepository.existsByBusinessRegistrationNumber(request.businessRegistrationNumber)) {
            throw BrandAlreadyExistsException(request.businessRegistrationNumber)
        }

        val user = userRepository.findById(userId).getOrElse { throw UserNotFoundException(userId) }

        val entity = Brand.createBySeller(request, user)
        return Brand.toResponse(brandRepository.save(entity))
    }

    override fun findAllBrandByOwnerUser(userId: String): List<BrandResponse> {
        return brandRepository.findAllByOwnerUserId(userId).map { Brand.toResponse(it) }
    }

    override fun findBrandDetail(id: String): GetBrandDetailResponse {
        val brand = getBrand(id)

        return Brand.toDetailResponse(brand)
    }

    override fun updateBrand(id: String, userId: String, request: UpdateBrandRequest): BrandResponse {
        val brand = getBrand(id, userId)

        val updated = brand.apply {
            name = request.name ?: name
            description = request.description ?: description
            fullAddress = request.fullAddress ?: fullAddress
            addressDetail = request.addressDetail ?: addressDetail
            province = request.province ?: province
            district = request.district ?: district
            neighborhood = request.neighborhood ?: neighborhood
            street = request.street ?: street
        }

        return Brand.toResponse(brandRepository.save(updated))
    }

    override fun deleteBrand(id: String, userId: String) {
        val brand = getBrand(id, userId)

        brandRepository.delete(brand)
    }

    private fun getBrand(id: String): Brand =
        brandRepository.findById(id).orElse(null)
            ?: throw BrandNotFoundException(id, null)

    private fun getBrand(id: String, userId: String): Brand =
        brandRepository.findByIdAndOwnerUserId(id, userId)
            ?: throw BrandNotFoundException(id, userId)
}
