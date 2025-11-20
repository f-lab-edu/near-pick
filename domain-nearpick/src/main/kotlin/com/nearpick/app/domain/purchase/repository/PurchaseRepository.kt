package com.nearpick.app.domain.purchase.repository

import com.nearpick.app.domain.purchase.entity.PurchaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PurchaseRepository : JpaRepository<PurchaseEntity, String> {
    fun findAllByUserId(userId: String): List<PurchaseEntity>
    fun findByIdAndUserId(id: String, userId: String): Optional<PurchaseEntity>
    fun findAllByProduct_Seller_Id(sellerId: String): List<PurchaseEntity>
    fun findByIdAndProduct_Seller_Id(id: String, sellerId: String): Optional<PurchaseEntity>
    fun existsByProduct_Id(productId: String): Boolean
}
