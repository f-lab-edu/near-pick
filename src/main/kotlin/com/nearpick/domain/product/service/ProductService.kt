package com.nearpick.domain.product.service

import com.nearpick.common.exception.BrandNotFoundException
import com.nearpick.common.exception.ProductNotFoundException
import com.nearpick.domain.brand.entity.Brand
import com.nearpick.domain.brand.repository.BrandRepository
import com.nearpick.domain.product.dto.CreateProductRequest
import com.nearpick.domain.product.dto.GetProductDetailResponse
import com.nearpick.domain.product.dto.ProductResponse
import com.nearpick.domain.product.dto.UpdateProductRequest
import com.nearpick.domain.product.entity.Product
import com.nearpick.domain.product.repository.ProductRepository
import com.nearpick.domain.user.entity.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductService(
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository
) {
    fun createProduct(request: CreateProductRequest, user: User): ProductResponse {
        val brand = getBrand(request.brandId, user.id)
        val entity = Product.createBySeller(request, user, brand)

        return Product.toResponse(productRepository.save(entity))
    }

    fun findAllProductByBrand(brandId: String): List<ProductResponse> {
        val brand = getBrand(brandId, null)

        return productRepository.findAllByBrand(brand).map { Product.toResponse(it) }
    }

    fun findProductDetail(id: String): GetProductDetailResponse {
        val product = getProduct(id)

        return Product.toDetailResponse(product)
    }

    fun updateProduct(id: String, user: User, request: UpdateProductRequest): ProductResponse {
        val product = getProduct(id)

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

    fun deleteProduct(id: String, user: User) {
        val product = getProduct(id)

        productRepository.delete(product)
    }

    private fun getBrand(brandId: String, userId: String?): Brand =
        brandRepository.findById(brandId).orElse(null)
            ?: throw BrandNotFoundException(brandId, userId)

    private fun getProduct(id: String): Product =
        productRepository.findById(id).orElse(null)
            ?: throw ProductNotFoundException(id, null)
}
