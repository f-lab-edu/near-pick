package com.nearpick.app.domain.purchase.mapper

import com.nearpick.app.common.constant.Role
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.user.dto.UserResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigInteger
import java.time.LocalDateTime

class PurchaseResponseMapperTest : StringSpec({

    val mapper = PurchaseResponseMapper()

    val purchase = Purchase(
        id = "purchase-id",
        userId = "user-id",
        productId = "product-id",
        productType = ProductType.FIRST_COME,
        totalPrice = BigInteger("10000"),
        quantity = 2,
        reservationDt = LocalDateTime.now(),
        status = PurchaseStatus.PENDING,
        requestMessage = "test"
    )

    "Purchase를 기준으로 PurchaseResponse 매핑이 올바르게 이루어진다." {
        val resp = mapper.toResponse(purchase)

        resp.id shouldBe purchase.id
        resp.productId shouldBe purchase.productId
        resp.productType shouldBe purchase.productType
        resp.totalPrice shouldBe purchase.totalPrice
        resp.quantity shouldBe purchase.quantity
        resp.reservationDt shouldBe purchase.reservationDt
        resp.status shouldBe purchase.status
        resp.requestMessage shouldBe purchase.requestMessage
    }

    "Purchase, UserResponse, ProductRespons을 기준으로 GetPurchaseDetailResponse 매핑이 올바르게 이루어진다." {
        val user = UserResponse(
            id = "u-123", email = "test@test.com", nickname = "닉네임",
            profileImageUrl = "test",
            phoneNumber = "010000000000",
            role = Role.SELLER.name,
            accountHolderName = "test",
            bankName = "OO은행",
            accountNumber = "00000-00000-00000",
            isActive = true
        )

        val product = ProductResponse(
            id = "prod-123",
            name = "상품명",
            description = "설명",
            price = BigInteger("15000"),
            stock = 10,
            productType = ProductType.FIRST_COME,
            reservationDeadline = null,
            status = null
        )

        val detail = mapper.toDetailResponseBySeller(purchase, user, product)

        detail.id shouldBe purchase.id
        detail.user shouldBe user
        detail.product shouldBe product
        detail.totalPrice shouldBe purchase.totalPrice
        detail.quantity shouldBe purchase.quantity
        detail.reservationDt shouldBe purchase.reservationDt
        detail.status shouldBe purchase.status
    }
})
