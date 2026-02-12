package com.nearpick.app.domain.product.service

import com.nearpick.app.common.exception.BrandNotFoundException
import com.nearpick.app.common.exception.ProductNotFoundException
import com.nearpick.app.common.exception.ProductReservationExistsException
import com.nearpick.app.domain.brand.mapper.BrandMapper
import com.nearpick.app.domain.brand.mapper.BrandResponseMapper
import com.nearpick.app.domain.brand.repository.BrandRepository
import com.nearpick.app.domain.product.dto.CreateProductRequest
import com.nearpick.app.domain.product.dto.GetProductDetailResponse
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.dto.UpdateProductRequest
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.mapper.ProductMapper
import com.nearpick.app.domain.product.mapper.ProductResponseMapper
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.purchase.repository.PurchaseRepository
import com.nearpick.app.domain.user.mapper.UserMapper
import com.nearpick.app.domain.user.mapper.UserResponseMapper
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional(readOnly = false)
open class ProductServiceImpl(
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
    private val purchaseRepository: PurchaseRepository,
    private val productMapper: ProductMapper,
    private val productResponseMapper: ProductResponseMapper,
    private val brandMapper: BrandMapper,
    private val brandResponseMapper: BrandResponseMapper,
    private val userMapper: UserMapper,
    private val userResponseMapper: UserResponseMapper
) : ProductService {

    override fun createProduct(request: CreateProductRequest, userId: String): ProductResponse {
        if (!brandRepository.existsById(request.brandId)) throw BrandNotFoundException(request.brandId, userId)

        val product = Product(
            sellerId = userId,
            brandId = request.brandId,
            name = request.name,
            description = request.description,
            price = request.price,
            stock = request.stock,
            productType = request.productType,
            startDt = request.startDt,
            endDt = request.endDt
        )
        val entity = productMapper.toEntity(product)
        productRepository.save(entity)

        return productResponseMapper.toResponse(product)
    }

    @Transactional(readOnly = true)
    override fun findAllProductByBrand(brandId: String): List<ProductResponse> {
        if (!brandRepository.existsById(brandId)) throw BrandNotFoundException(brandId, null)

        return productRepository.findAllByBrandEntity_Id(brandId)
            .map { productResponseMapper.toResponse(productMapper.toDomain(it)) }
    }

    @Transactional(readOnly = true)
    override fun findProductDetail(id: String): GetProductDetailResponse {
        val entity = productRepository.findById(id).orElse(null)
            ?: throw ProductNotFoundException(id, null)

        val product = productMapper.toDomain(entity)

        val user = userMapper.toDomain(entity.seller)
        val userResponse = userResponseMapper.toResponse(user)

        val brand = brandMapper.toDomain(entity.brandEntity)
        val brandResponse = brandResponseMapper.toResponse(brand)

        return productResponseMapper.toDetailResponse(product, userResponse, brandResponse)
    }

    override fun updateProduct(id: String, userId: String, request: UpdateProductRequest): ProductResponse {
        val entity = productRepository.findByIdAndSellerId(id, userId)
            ?: throw ProductNotFoundException(id, userId)

        val product = productMapper.toDomain(entity)

        product.update(
            request.name,
            request.description,
            request.price,
            request.stock,
            request.productType,
            request.startDt,
            request.endDt
        )

        val updatedEntity = productMapper.toEntity(product)
        productRepository.save(updatedEntity)

        return productResponseMapper.toResponse(product)
    }

    override fun deleteProduct(id: String, userId: String) {
        if (purchaseRepository.existsByProduct_Id(id)) {
            throw ProductReservationExistsException(id)
        }

        val entity = productRepository.findByIdAndSellerId(id, userId)
            ?: throw ProductNotFoundException(id, userId)

        val product = productMapper.toDomain(entity)

        product.updateStatus(ProductStatus.DELETE)

        productRepository.save(entity)
    }
}
