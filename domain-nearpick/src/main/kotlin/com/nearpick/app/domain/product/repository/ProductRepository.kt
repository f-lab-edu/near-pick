package com.nearpick.app.domain.product.repository

import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, String> {
    fun findAllByBrandEntity(brandEntity: BrandEntity): List<Product>
    fun findByIdAndSellerId(id: String, userId: String): Product?
}
