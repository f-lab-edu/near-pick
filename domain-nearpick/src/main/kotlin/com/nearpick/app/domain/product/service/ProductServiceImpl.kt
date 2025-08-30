package com.nearpick.app.domain.product.service

import com.nearpick.app.common.exception.BrandNotFoundException
import com.nearpick.app.common.exception.ProductNotFoundException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.brand.repository.BrandRepository
import com.nearpick.app.domain.product.dto.CreateProductRequest
import com.nearpick.app.domain.product.dto.GetProductDetailResponse
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.dto.UpdateProductRequest
import com.nearpick.app.domain.product.entity.Product
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = true)
open class ProductServiceImpl(
    private val userRepository: UserRepository,
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository
) : ProductService {

    @Transactional
    override fun createProduct(request: CreateProductRequest, userId: String): ProductResponse {
        val brand = getBrand(request.brandId, userId)
        val user = userRepository.findById(userId).getOrElse { throw UserNotFoundException(userId) }
        val entity = Product.createBySeller(request, user, brand)

        return Product.toResponse(productRepository.save(entity))
    }

    override fun findAllProductByBrand(brandId: String): List<ProductResponse> {
        val brand = getBrand(brandId)

        return productRepository.findAllByBrand(brand).map { Product.toResponse(it) }
    }

    override fun findProductDetail(id: String): GetProductDetailResponse {
        val product = getProduct(id)

        return Product.toDetailResponse(product)
    }

    @Transactional
    override fun updateProduct(id: String, userId: String, request: UpdateProductRequest): ProductResponse {
        val product = getProduct(id, userId)

        val updated = product.apply {
            name = request.name ?: name
            description = request.description ?: description
            price = request.price ?: price
            stock = request.stock ?: stock
            productType = request.productType ?: productType
            reservationDeadline = request.reservationDeadline ?: reservationDeadline
            isActive = request.isActive ?: isActive
        }

        return Product.toResponse(productRepository.save(updated))
    }

    @Transactional
    override fun deleteProduct(id: String, userId: String) {
        val product = getProduct(id, userId)

        productRepository.delete(product)
    }

    private fun getBrand(brandId: String): BrandEntity =
        brandRepository.findById(brandId).getOrElse { throw BrandNotFoundException(brandId, null) }

    private fun getBrand(brandId: String, userId: String): BrandEntity =
        brandRepository.findByIdAndOwnerUserId(brandId, userId) ?: throw BrandNotFoundException(brandId, userId)

    private fun getProduct(id: String): Product =
        productRepository.findById(id).orElse(null)
            ?: throw ProductNotFoundException(id, null)

    private fun getProduct(id: String, userId: String): Product =
        productRepository.findByIdAndSellerId(id, userId) ?: throw ProductNotFoundException(id, userId)
}
