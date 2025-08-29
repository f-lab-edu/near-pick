package com.nearpick.app.domain.product.service

import com.nearpick.app.domain.product.dto.CreateProductRequest
import com.nearpick.app.domain.product.dto.GetProductDetailResponse
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.dto.UpdateProductRequest

interface ProductService {
    fun createProduct(request: CreateProductRequest, userId: String): ProductResponse
    fun findAllProductByBrand(brandId: String): List<ProductResponse>
    fun findProductDetail(id: String): GetProductDetailResponse
    fun updateProduct(id: String, userId: String, request: UpdateProductRequest): ProductResponse
    fun deleteProduct(id: String, userId: String)
}
