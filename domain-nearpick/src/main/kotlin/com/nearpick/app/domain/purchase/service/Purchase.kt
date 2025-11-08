package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.PurchaseStatusInvalidRoleException
import com.nearpick.app.common.exception.PurchaseStatusInvalidTransitionException
import com.nearpick.app.domain.product.dto.ProductFirstComeCache
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailBySellerResponse
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailByUserResponse
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.entity.PurchaseEntity
import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.product.service.Product
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.user.entity.UserEntity
import com.nearpick.app.domain.user.service.User
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.UUID

class Purchase(
    val id: String? = null,
    val user: UserEntity,
    val product: ProductEntity,
    val productType: ProductType,
    var totalPrice: BigInteger,
    var quantity: Int,
    var reservationDt: LocalDateTime? = null,
    var status: PurchaseStatus,
    var requestMessage: String? = null
) {
    fun update(request: UpdatePurchaseRequest) {
        this.totalPrice = request.price
        this.quantity = request.quantity
        this.reservationDt = request.reservationDt
        this.requestMessage = request.requestMessage
    }

    fun updateStatus(userRole: Role, status: PurchaseStatus) {
        val allowedByRole = mapOf(
            Role.USER   to setOf(PurchaseStatus.CANCELLED),
            Role.SELLER to setOf(PurchaseStatus.CONFIRMED, PurchaseStatus.CANCELLED, PurchaseStatus.SUCCESS)
        )

        require(allowedByRole[userRole]?.contains(status) == true) {
            throw PurchaseStatusInvalidRoleException(userRole.name, status.name)
        }

        val transitionRule = mapOf(
            PurchaseStatus.PENDING   to setOf(PurchaseStatus.CANCELLED, PurchaseStatus.CONFIRMED),
            PurchaseStatus.CONFIRMED to setOf(PurchaseStatus.SUCCESS, PurchaseStatus.CANCELLED),
            PurchaseStatus.CANCELLED to emptySet(),
            PurchaseStatus.SUCCESS   to emptySet()
        )

        require(transitionRule[this.status]?.contains(status) == true) {
            throw PurchaseStatusInvalidTransitionException(this.status.name, status.name)
        }

        this.status = status
    }

    fun toEntity(): PurchaseEntity {
        return PurchaseEntity(
            id = this.id ?: UUID.randomUUID().toString(),
            user = this.user,
            product = this.product,
            productType = this.productType,
            totalPrice = this.totalPrice,
            quantity = this.quantity,
            reservationDt = this.reservationDt,
            status = status,
            requestMessage = this.requestMessage
        )
    }

    companion object {
        fun ofOrderReceived(
            createPurchaseRequest: CreatePurchaseRequest,
            productEntity: ProductFirstComeCache
        ): OrderReceivedResponse {
            return OrderReceivedResponse(
                id = null,
                productId = createPurchaseRequest.productId,
                productType = productEntity.productType,
                totalPrice = productEntity.price.multiply(createPurchaseRequest.quantity.toBigInteger()),
                quantity = createPurchaseRequest.quantity,
                reservationDt = createPurchaseRequest.reservationDt,
                status = PurchaseStatus.PENDING,
                requestMessage = createPurchaseRequest.requestMessage
            )
        }

        fun ofOrderReceived(
            createPurchaseRequest: CreatePurchaseRequest,
            productEntity: ProductEntity
        ): OrderReceivedResponse {
            return OrderReceivedResponse(
                id = null,
                productId = createPurchaseRequest.productId,
                productType = productEntity.productType,
                totalPrice = productEntity.price.multiply(createPurchaseRequest.quantity.toBigInteger()),
                quantity = createPurchaseRequest.quantity,
                reservationDt = createPurchaseRequest.reservationDt,
                status = PurchaseStatus.PENDING,
                requestMessage = createPurchaseRequest.requestMessage
            )
        }

        fun create(
            createPurchaseRequest: CreatePurchaseRequest,
            userEntity: UserEntity,
            productEntity: ProductEntity
        ): Purchase {
            return Purchase(
                id = UUID.randomUUID().toString(),
                user = userEntity,
                product = productEntity,
                productType = productEntity.productType,
                totalPrice = productEntity.price.multiply(createPurchaseRequest.quantity.toBigInteger()),
                quantity = createPurchaseRequest.quantity,
                reservationDt = createPurchaseRequest.reservationDt,
                status = PurchaseStatus.PENDING,
                requestMessage = createPurchaseRequest.requestMessage
            )
        }

        fun from(purchaseEntity: PurchaseEntity): Purchase {
            return Purchase(
                id = purchaseEntity.id,
                user = purchaseEntity.user,
                product = purchaseEntity.product,
                productType = purchaseEntity.productType,
                totalPrice = purchaseEntity.totalPrice,
                quantity = purchaseEntity.quantity,
                reservationDt = purchaseEntity.reservationDt,
                status = purchaseEntity.status,
                requestMessage = purchaseEntity.requestMessage
            )
        }

        fun toResponse(purchaseEntity: PurchaseEntity): PurchaseResponse {
            return PurchaseResponse(
                id = purchaseEntity.id,
                productId = purchaseEntity.product.id,
                productType = purchaseEntity.productType,
                totalPrice = purchaseEntity.totalPrice,
                quantity = purchaseEntity.quantity,
                reservationDt = purchaseEntity.reservationDt,
                status = purchaseEntity.status,
                requestMessage = purchaseEntity.requestMessage
            )
        }

        fun toDetailResponseBySeller(purchaseEntity: PurchaseEntity): GetPurchaseDetailBySellerResponse {
            return GetPurchaseDetailBySellerResponse(
                id = purchaseEntity.id,
                user = User.toResponse(purchaseEntity.user),
                product = Product.toResponse(purchaseEntity.product),
                totalPrice = purchaseEntity.totalPrice,
                quantity = purchaseEntity.quantity,
                reservationDt = purchaseEntity.reservationDt,
                status = purchaseEntity.status,
                requestMessage = purchaseEntity.requestMessage
            )
        }

        fun toDetailResponseByUser(purchaseEntity: PurchaseEntity): GetPurchaseDetailByUserResponse {
            return GetPurchaseDetailByUserResponse(
                id = purchaseEntity.id,
                product = Product.toResponse(purchaseEntity.product),
                totalPrice = purchaseEntity.totalPrice,
                quantity = purchaseEntity.quantity,
                reservationDt = purchaseEntity.reservationDt,
                status = purchaseEntity.status,
                requestMessage = purchaseEntity.requestMessage
            )
        }
    }
}
