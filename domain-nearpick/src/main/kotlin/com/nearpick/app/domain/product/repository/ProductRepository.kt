package com.nearpick.app.domain.product.repository

import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<ProductEntity, String> {
    fun findAllByBrandEntity_Id(brandId: String): List<ProductEntity>
    fun findByIdAndSellerId(id: String, userId: String): ProductEntity?
    fun findAllByProductTypeAndStatus(productType: ProductType, status: ProductStatus): List<ProductEntity>

    @Modifying
    @Query("UPDATE ProductEntity p SET p.stock = p.stock - :quantity WHERE p.id = :productId AND p.stock >= :quantity")
    fun decreaseStock(@Param("productId") productId: String, @Param("quantity") quantity: Int): Int

    @Modifying
    @Query("UPDATE ProductEntity p SET p.status = 'INACTIVE_SOLD_OUT' WHERE p.id = :productId AND p.stock = 0")
    fun markSoldOutIfEmpty(@Param("productId") productId: String): Int

    @Modifying
    @Query("UPDATE ProductEntity p SET p.status = :status WHERE p.id = :productId")
    fun updateStatusById(@Param("productId") productId: String, @Param("status") status: ProductStatus): Int
}
