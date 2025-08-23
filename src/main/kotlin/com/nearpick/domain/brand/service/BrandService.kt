package com.nearpick.domain.brand.service

import com.nearpick.common.exception.BrandAlreadyExistsException
import com.nearpick.common.exception.BrandNotFoundException
import com.nearpick.domain.brand.dto.BrandResponse
import com.nearpick.domain.brand.dto.CreateBrandRequest
import com.nearpick.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.domain.brand.dto.UpdateBrandRequest
import com.nearpick.domain.brand.entity.Brand
import com.nearpick.domain.brand.repository.BrandRepository
import com.nearpick.domain.user.entity.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class BrandService(
    private val brandRepository: BrandRepository
) {
    fun createBrand(request: CreateBrandRequest, user: User): BrandResponse {
        if (brandRepository.existsByBusinessRegistrationNumber(request.businessRegistrationNumber)) {
            throw BrandAlreadyExistsException(request.businessRegistrationNumber)
        }

        val entity = Brand.createBySeller(request, user)
        return Brand.toResponse(brandRepository.save(entity))
    }

    fun findAllBrandByOwnerUser(user: User): List<BrandResponse> {
        return brandRepository.findAllByOwnerUser(user).map { Brand.toResponse(it) }
    }

    fun findBrandDetail(id: String): GetBrandDetailResponse {
        val brand = getBrand(id)

        return Brand.toDetailResponse(brand)
    }

    fun updateBrand(id: String, user: User, request: UpdateBrandRequest): BrandResponse {
        val brand = getBrand(id, user)

        val updated = brand.apply {
            name = request.name
            description = request.description
            fullAddress = request.fullAddress
            addressDetail = request.addressDetail
            province = request.province
            district = request.district
            neighborhood = request.neighborhood
            street = request.street
        }

        return Brand.toResponse(brandRepository.save(updated))
    }

    fun deleteBrand(id: String, user: User) {
        val brand = getBrand(id, user)

        brandRepository.delete(brand)
    }

    private fun getBrand(id: String): Brand =
        brandRepository.findById(id).orElse(null)
            ?: throw BrandNotFoundException(id, null)

    private fun getBrand(id: String, user: User): Brand =
        brandRepository.findByIdAndOwnerUser(id, user)
            ?: throw BrandNotFoundException(id, user.id)
}
