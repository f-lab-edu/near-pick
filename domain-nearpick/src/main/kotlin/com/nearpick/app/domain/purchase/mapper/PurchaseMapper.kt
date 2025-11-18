package com.nearpick.app.domain.purchase.mapper

import com.nearpick.app.domain.brand.repository.BrandRepository
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.purchase.entity.PurchaseEntity
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PurchaseMapper(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) {
    fun toEntity(purchase: Purchase): PurchaseEntity {
        val userProxy = userRepository.getReferenceById(purchase.userId)
        val productProxy = productRepository.getReferenceById(purchase.productId)

        return PurchaseEntity(
            id = purchase.id ?: UUID.randomUUID().toString(),
            user = userProxy,
            product = productProxy,
            productType = purchase.productType,
            totalPrice = purchase.totalPrice,
            quantity = purchase.quantity,
            reservationDt = purchase.reservationDt,
            status = purchase.status,
            requestMessage = purchase.requestMessage
        )
    }

    fun toDomain(entity: PurchaseEntity): Purchase {
        return Purchase(
            id = entity.id,
            userId = entity.user.id,
            productId = entity.product.id,
            productType = entity.productType,
            totalPrice = entity.totalPrice,
            quantity = entity.quantity,
            reservationDt = entity.reservationDt,
            status = entity.status,
            requestMessage = entity.requestMessage
        )
    }
}
