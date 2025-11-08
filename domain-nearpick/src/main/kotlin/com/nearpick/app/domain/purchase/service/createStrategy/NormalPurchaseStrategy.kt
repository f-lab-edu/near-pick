package com.nearpick.app.domain.purchase.service.createStrategy

import com.nearpick.app.common.exception.ProductNotFoundException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.repository.PurchaseRepository
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class NormalPurchaseStrategy(
    private val purchaseRepository: PurchaseRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : PurchaseStrategy {

    override fun createPurchase(request: CreatePurchaseRequest, userId: String): OrderReceivedResponse {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException(userId) }
        val product = productRepository.findById(request.productId)
            .orElseThrow { ProductNotFoundException(request.productId, userId) }

        val purchase = Purchase.create(request, user, product)
        purchaseRepository.save(purchase.toEntity())

        return Purchase.ofOrderReceived(request, product)
    }
}
