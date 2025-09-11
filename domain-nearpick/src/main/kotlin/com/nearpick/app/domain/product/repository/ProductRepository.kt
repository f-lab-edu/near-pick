package com.nearpick.app.domain.product.repository

import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<ProductEntity, String> {
    fun findAllByBrandEntity(brandEntity: BrandEntity): List<ProductEntity>
    fun findByIdAndSellerId(id: String, userId: String): ProductEntity?
}
