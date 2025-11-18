package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.PurchaseStatusInvalidRoleException
import com.nearpick.app.common.exception.PurchaseStatusInvalidTransitionException
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailResponse
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.entity.PurchaseEntity
import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.product.service.Product
import com.nearpick.app.domain.user.entity.UserEntity
import com.nearpick.app.domain.user.service.User
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

    fun updateStatus(userRole: Role, status: PurchaseStatus) {
        val allowedByRole = mapOf(
            Role.USER to setOf(PurchaseStatus.CANCELLED),
            Role.SELLER to setOf(PurchaseStatus.CONFIRMED, PurchaseStatus.CANCELLED, PurchaseStatus.SUCCESS)
        )

        require(allowedByRole[userRole]?.contains(status) == true) {
            throw PurchaseStatusInvalidRoleException(userRole.name, status.name)
        }

        val transitionRule = mapOf(
            PurchaseStatus.PENDING to setOf(PurchaseStatus.CANCELLED, PurchaseStatus.CONFIRMED),
            PurchaseStatus.CONFIRMED to setOf(PurchaseStatus.SUCCESS, PurchaseStatus.CANCELLED),
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
    }
}
