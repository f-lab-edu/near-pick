package com.nearpick.app.domain.purchase.service.createStrategy

import com.nearpick.app.common.exception.ProductNotFoundException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.mapper.PurchaseMapper
import com.nearpick.app.domain.purchase.repository.PurchaseRepository
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class NormalPurchaseStrategy(
    private val purchaseRepository: PurchaseRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val purchaseMapper: PurchaseMapper
) : PurchaseStrategy {

    override fun createPurchase(request: CreatePurchaseRequest, userId: String): OrderReceivedResponse {
        if (!userRepository.existsById(userId)) throw UserNotFoundException(userId)
        if (!productRepository.existsById(request.productId)) throw ProductNotFoundException(request.productId, userId)

        val purchase = Purchase.create(
            userId = userId,
            productId = request.productId,
            productType = request.productType,
            price = request.price,
            quantity = request.quantity,
            reservationDt = request.reservationDt,
            message = request.requestMessage
        )
        purchaseRepository.save(purchaseMapper.toEntity(purchase))

        return Purchase.ofOrderReceived(request)
    }
}
