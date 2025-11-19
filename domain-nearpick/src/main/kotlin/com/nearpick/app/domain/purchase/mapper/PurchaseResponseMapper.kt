package com.nearpick.app.domain.purchase.mapper

import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailResponse
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.user.dto.UserResponse
import org.springframework.stereotype.Component

@Component
class PurchaseResponseMapper {
    fun toResponse(purchase: Purchase): PurchaseResponse {
        return PurchaseResponse(
            id = purchase.id,
            productId = purchase.productId,
            productType = purchase.productType,
            totalPrice = purchase.totalPrice,
            quantity = purchase.quantity,
            reservationDt = purchase.reservationDt,
            status = purchase.status,
            requestMessage = purchase.requestMessage
        )
    }

    fun toDetailResponseBySeller(purchase: Purchase, user: UserResponse, product: ProductResponse): GetPurchaseDetailResponse {
        return GetPurchaseDetailResponse(
            id = purchase.id,
            user = user,
            product = product,
            totalPrice = purchase.totalPrice,
            quantity = purchase.quantity,
            reservationDt = purchase.reservationDt,
            status = purchase.status,
            requestMessage = purchase.requestMessage
        )
    }

    fun toDetailResponseByUser(purchase: Purchase, user: UserResponse, product: ProductResponse): GetPurchaseDetailResponse {
        return GetPurchaseDetailResponse(
            id = purchase.id,
            user = user,
            product = product,
            totalPrice = purchase.totalPrice,
            quantity = purchase.quantity,
            reservationDt = purchase.reservationDt,
            status = purchase.status,
            requestMessage = purchase.requestMessage
        )
    }
}
