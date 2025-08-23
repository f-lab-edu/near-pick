package com.nearpick.domain.product.repository

import com.nearpick.domain.brand.entity.Brand
import com.nearpick.domain.product.entity.Product
import com.nearpick.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, String> {
    fun findAllByBrand(brand: Brand): List<Product>
    fun findByIdAndSeller(id: String, user: User): Product?
}
