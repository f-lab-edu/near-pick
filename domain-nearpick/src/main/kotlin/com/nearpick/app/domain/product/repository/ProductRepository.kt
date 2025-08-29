package com.nearpick.app.domain.product.repository

import com.nearpick.app.domain.brand.entity.Brand
import com.nearpick.app.domain.product.entity.Product
import com.nearpick.app.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, String> {
    fun findAllByBrand(brand: Brand): List<Product>
    fun findByIdAndSellerId(id: String, userId: String): Product?
}
