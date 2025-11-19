package com.nearpick.app.domain.brand.service

import com.nearpick.app.common.exception.BrandAlreadyExistsException
import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.CreateBrandRequest
import com.nearpick.app.domain.brand.repository.BrandRepository
import com.nearpick.app.common.exception.BrandNotFoundException
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.brand.dto.UpdateBrandRequest
import com.nearpick.app.domain.brand.mapper.BrandMapper
import com.nearpick.app.domain.brand.mapper.BrandResponseMapper
import com.nearpick.app.domain.user.mapper.UserMapper
import com.nearpick.app.domain.user.mapper.UserResponseMapper
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional(readOnly = false)
open class BrandServiceImpl(
    private val brandRepository: BrandRepository,
    private val brandMapper: BrandMapper,
    private val brandResponseMapper: BrandResponseMapper,
    private val userMapper: UserMapper,
    private val userResponseMapper: UserResponseMapper
) : BrandService {

    override fun createBrand(request: CreateBrandRequest, userId: String): BrandResponse {
        if (brandRepository.existsByBusinessRegistrationNumber(request.businessRegistrationNumber)) {
            throw BrandAlreadyExistsException(request.businessRegistrationNumber)
        }

        val brand = Brand(
            name = request.name,
            ownerUserId = userId,
            description = request.description,
            businessRegistrationNumber = request.businessRegistrationNumber,
            fullAddress = request.fullAddress,
            addressDetail = request.addressDetail,
            province = request.province,
            district = request.district,
            neighborhood = request.neighborhood,
            street = request.street
        )

        val entity = brandMapper.toEntity(brand)
        brandRepository.save(entity)

        return brandResponseMapper.toResponse(brand)
    }

    @Transactional(readOnly = true)
    override fun findAllBrandByOwnerUser(userId: String): List<BrandResponse> {
        return brandRepository.findAllByOwnerUserEntityId(userId)
            .map { brandResponseMapper.toResponse(brandMapper.toDomain(it)) }
    }

    @Transactional(readOnly = true)
    override fun findBrandDetail(id: String): GetBrandDetailResponse {
        val entity = brandRepository.findById(id).orElse(null)
            ?: throw BrandNotFoundException(id, null)

        val brand = brandMapper.toDomain(entity)

        val user = userMapper.toDomain(entity.ownerUserEntity)
        val userResponse = userResponseMapper.toResponse(user)

        return brandResponseMapper.toDetailResponse(brand, userResponse)
    }

    override fun updateBrand(id: String, userId: String, request: UpdateBrandRequest): BrandResponse {
        val entity = brandRepository.findByIdAndOwnerUserEntityId(id, userId)
            ?: throw BrandNotFoundException(id, userId)

        val brand = brandMapper.toDomain(entity)

        brand.update(
            request.name,
            request.description,
            request.fullAddress,
            request.addressDetail,
            request.province,
            request.district,
            request.neighborhood,
            request.street
        )
        val updatedEntity = brandMapper.toEntity(brand)
        brandRepository.save(updatedEntity)

        return brandResponseMapper.toResponse(brand)
    }

    override fun deleteBrand(id: String, userId: String) {
        val entity = brandRepository.findByIdAndOwnerUserEntityId(id, userId)
            ?: throw BrandNotFoundException(id, userId)

        brandRepository.delete(entity)
    }
}
