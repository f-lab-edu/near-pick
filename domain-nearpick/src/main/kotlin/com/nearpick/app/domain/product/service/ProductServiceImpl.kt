package com.nearpick.app.domain.product.service

import com.nearpick.app.common.exception.BrandNotFoundException
import com.nearpick.app.common.exception.ProductNotFoundException
import com.nearpick.app.common.exception.ProductReservationExistsException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.brand.repository.BrandRepository
import com.nearpick.app.domain.product.dto.CreateProductRequest
import com.nearpick.app.domain.product.dto.GetProductDetailResponse
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.dto.UpdateProductRequest
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.purchase.repository.PurchaseRepository
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = false)
open class ProductServiceImpl(
    private val userRepository: UserRepository,
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
    private val purchaseRepository: PurchaseRepository
) : ProductService {

    override fun createProduct(request: CreateProductRequest, userId: String): ProductResponse {
        val brandEntity = getBrand(request.brandId, userId)
        val user = userRepository.findById(userId).getOrElse { throw UserNotFoundException(userId) }

        val product = Product(
            seller = user,
            brandEntity = brandEntity,
            name = request.name,
            description = request.description,
            price = request.price,
            stock = request.stock,
            productType = request.productType,
            startDt = request.startDt,
            endDt = request.endDt
        )

        return Product.toResponse(productRepository.save(product.toEntity()))
    }

    @Transactional(readOnly = true)
    override fun findAllProductByBrand(brandId: String): List<ProductResponse> {
        val brandEntity = getBrand(brandId)

        return productRepository.findAllByBrandEntity(brandEntity).map { Product.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun findProductDetail(id: String): GetProductDetailResponse {
        val productEntity = getProduct(id)

        return Product.toDetailResponse(productEntity)
    }

    override fun updateProduct(id: String, userId: String, request: UpdateProductRequest): ProductResponse {
        val productEntity = getProduct(id, userId)
        val product = Product.from(productEntity)

        product.update(request)

        return Product.toResponse(productRepository.save(product.toEntity()))
    }

    override fun deleteProduct(id: String, userId: String) {
        val productEntity = getProduct(id, userId)
        val product = Product.from(productEntity)

        if(purchaseRepository.existsByProduct(productEntity)) {
            throw ProductReservationExistsException(id)
        }

        product.updateStatus(ProductStatus.DELETE)

        productRepository.save(product.toEntity())
    }

    private fun getBrand(brandId: String): BrandEntity =
        brandRepository.findById(brandId).getOrElse { throw BrandNotFoundException(brandId, null) }

    private fun getBrand(brandId: String, userId: String): BrandEntity =
        brandRepository.findByIdAndOwnerUserEntityId(brandId, userId) ?: throw BrandNotFoundException(brandId, userId)

    private fun getProduct(id: String): ProductEntity =
        productRepository.findById(id).orElse(null)
            ?: throw ProductNotFoundException(id, null)

    private fun getProduct(id: String, userId: String): ProductEntity =
        productRepository.findByIdAndSellerId(id, userId) ?: throw ProductNotFoundException(id, userId)
}
