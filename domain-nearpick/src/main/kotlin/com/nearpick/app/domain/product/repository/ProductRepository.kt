package com.nearpick.app.domain.product.repository

import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<ProductEntity, String> {
    fun findAllByBrandEntity_Id(brandId: String): List<ProductEntity>
    fun findByIdAndSellerId(id: String, userId: String): ProductEntity?
    fun findAllByProductTypeAndStatus(productType: ProductType, status: ProductStatus): List<ProductEntity>

}
