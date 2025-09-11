package com.nearpick.app.domain.brand.service

import com.nearpick.app.common.exception.BrandAlreadyExistsException
import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.CreateBrandRequest
import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.brand.repository.BrandRepository
import com.nearpick.app.common.exception.BrandNotFoundException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.brand.dto.UpdateBrandRequest
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = false)
open class BrandServiceImpl(
    private val userRepository: UserRepository,
    private val brandRepository: BrandRepository
) : BrandService {

    override fun createBrand(request: CreateBrandRequest, userId: String): BrandResponse {
        val user = userRepository.findById(userId).getOrElse { throw UserNotFoundException(userId) }

        val brand = Brand(
            name = request.name,
            ownerUserEntity = user,
            description = request.description,
            businessRegistrationNumber = request.businessRegistrationNumber,
            fullAddress = request.fullAddress,
            addressDetail = request.addressDetail,
            province = request.province,
            district = request.district,
            neighborhood = request.neighborhood,
            street = request.street
        )

        if (brandRepository.existsByBusinessRegistrationNumber(request.businessRegistrationNumber)) {
            throw BrandAlreadyExistsException(request.businessRegistrationNumber)
        }

        return Brand.toResponse(brandRepository.save(brand.toEntity()))
    }

    @Transactional(readOnly = true)
    override fun findAllBrandByOwnerUser(userId: String): List<BrandResponse> {
        return brandRepository.findAllByOwnerUserEntityId(userId).map { Brand.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun findBrandDetail(id: String): GetBrandDetailResponse {
        val brand = getBrand(id)

        return Brand.toDetailResponse(brand)
    }

    override fun updateBrand(id: String, userId: String, request: UpdateBrandRequest): BrandResponse {
        val brandEntity = getBrand(id, userId)
        val brand = Brand.from(brandEntity)

        brand.update(request)

        return Brand.toResponse(brandRepository.save(brand.toEntity()))
    }

    override fun deleteBrand(id: String, userId: String) {
        val brand = getBrand(id, userId)

        brandRepository.delete(brand)
    }

    private fun getBrand(id: String): BrandEntity =
        brandRepository.findById(id).orElse(null)
            ?: throw BrandNotFoundException(id, null)

    private fun getBrand(id: String, userId: String): BrandEntity =
        brandRepository.findByIdAndOwnerUserEntityId(id, userId)
            ?: throw BrandNotFoundException(id, userId)
}
