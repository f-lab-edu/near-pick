package com.nearpick.app.domain.purchase.mapper

import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.purchase.entity.PurchaseEntity
import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.user.entity.UserEntity
import com.nearpick.app.domain.user.repository.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.UUID

class PurchaseMapperTest : StringSpec({

    val userRepository = mock(UserRepository::class.java)
    val productRepository = mock(ProductRepository::class.java)

    val mapper = PurchaseMapper(userRepository, productRepository)

    val userId = UUID.randomUUID().toString()
    val productId = UUID.randomUUID().toString()

    val userProxy = mock(UserEntity::class.java)
    val productProxy = mock(ProductEntity::class.java)

    `when`(userProxy.id).thenReturn(userId)
    `when`(productProxy.id).thenReturn(productId)

    `when`(userRepository.getReferenceById(userId)).thenReturn(userProxy)
    `when`(productRepository.getReferenceById(productId)).thenReturn(productProxy)

    val purchase = Purchase(
        id = null,
        userId = userId,
        productId = productId,
        productType = ProductType.FIRST_COME,
        totalPrice = BigInteger("10000"),
        quantity = 2,
        reservationDt = LocalDateTime.now(),
        status = PurchaseStatus.PENDING,
        requestMessage = "test"
    )

    "Purchase에서 PurchaseEntity 매핑이 정상적으로 이루어진다" {
        val entity = mapper.toEntity(purchase)

        entity.user shouldBe userProxy
        entity.product shouldBe productProxy
        entity.productType shouldBe purchase.productType
        entity.totalPrice shouldBe purchase.totalPrice
        entity.quantity shouldBe purchase.quantity
        entity.reservationDt shouldBe purchase.reservationDt
        entity.status shouldBe purchase.status
        entity.requestMessage shouldBe purchase.requestMessage
    }

    "PurchaseEntity에서 Purchase 매핑이 정상적으로 이루어진다" {
        val entity = PurchaseEntity(
            id = "purchase-id",
            user = userProxy,
            product = productProxy,
            productType = ProductType.FIRST_COME,
            totalPrice = BigInteger("10000"),
            quantity = 2,
            reservationDt = LocalDateTime.now(),
            status = PurchaseStatus.CONFIRMED,
            requestMessage = "test"
        )

        val domain = mapper.toDomain(entity)

        domain.id shouldBe entity.id
        domain.userId shouldBe userId
        domain.productId shouldBe productId
        domain.productType shouldBe entity.productType
        domain.totalPrice shouldBe entity.totalPrice
        domain.quantity shouldBe entity.quantity
        domain.reservationDt shouldBe entity.reservationDt
        domain.status shouldBe entity.status
        domain.requestMessage shouldBe entity.requestMessage
    }
})
