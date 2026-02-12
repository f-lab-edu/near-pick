package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.InvalidRoleException
import com.nearpick.app.common.exception.PurchaseStatusInvalidRoleException
import com.nearpick.app.common.exception.PurchaseStatusInvalidTransitionException
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.product.service.Product
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.UUID

class Purchase(
    val id: String? = null,
    val userId: String,
    val productId: String,
    val productType: ProductType,
    var totalPrice: BigInteger,
    var quantity: Int,
    var reservationDt: LocalDateTime? = null,
    var status: PurchaseStatus,
    var requestMessage: String? = null
) {
    fun update(
        price: BigInteger,
        quantity: Int,
        reservationDt: LocalDateTime?,
        message: String?
    ) {
        this.totalPrice = price
        this.quantity = quantity
        this.reservationDt = reservationDt
        this.requestMessage = message
    }

    fun verifyAccess(userId: String, role: Role, sellerId: String) {
        val isSeller = role == Role.SELLER && sellerId == userId
        val isUser = role == Role.USER && this.userId == userId

        if (!isSeller && !isUser) {
            throw InvalidRoleException(role.name)
        }
    }

    fun updateStatus(userRole: Role, status: PurchaseStatus) {
        val allowedByRole = mapOf(
            Role.USER to setOf(PurchaseStatus.CANCELLED),
            Role.SELLER to setOf(PurchaseStatus.PENDING, PurchaseStatus.CONFIRMED, PurchaseStatus.CANCELLED, PurchaseStatus.SUCCESS)
        )

        require(allowedByRole[userRole]?.contains(status) == true) {
            throw PurchaseStatusInvalidRoleException(userRole.name, status.name)
        }

        val transitionRule = mapOf(
            PurchaseStatus.PENDING to setOf(PurchaseStatus.CANCELLED, PurchaseStatus.CONFIRMED),
            PurchaseStatus.CONFIRMED to setOf(PurchaseStatus.PENDING, PurchaseStatus.SUCCESS, PurchaseStatus.CANCELLED),
            PurchaseStatus.CANCELLED to emptySet(),
            PurchaseStatus.SUCCESS to emptySet()
        )

        require(transitionRule[this.status]?.contains(status) == true) {
            throw PurchaseStatusInvalidTransitionException(this.status.name, status.name)
        }

        this.status = status
    }

    companion object {
        fun create(
            userId: String,
            productId: String,
            productType: ProductType,
            price: BigInteger,
            quantity: Int,
            reservationDt: LocalDateTime?,
            message: String?
        ): Purchase {
            return Purchase(
                id = UUID.randomUUID().toString(),
                userId = userId,
                productId = productId,
                productType = productType,
                totalPrice = price.multiply(quantity.toBigInteger()),
                quantity = quantity,
                reservationDt = reservationDt,
                status = PurchaseStatus.PENDING,
                requestMessage = message
            )
        }

        fun ofOrderReceived(request: CreatePurchaseRequest): OrderReceivedResponse {
            return OrderReceivedResponse(
                productId = request.productId,
                productType = request.productType,
                totalPrice = request.price.multiply(request.quantity.toBigInteger()),
                quantity = request.quantity,
                reservationDt = request.reservationDt,
                status = PurchaseStatus.PENDING,
                requestMessage = request.requestMessage
            )
        }
    }
}
