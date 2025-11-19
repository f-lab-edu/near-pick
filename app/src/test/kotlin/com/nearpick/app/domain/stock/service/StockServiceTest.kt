package com.nearpick.app.domain.stock.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.product.service.Product
import com.nearpick.app.domain.user.entity.UserEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.UUID

class StockServiceTest : StringSpec({
    val productRepository = mock<ProductRepository>()

//    "선착순 구매 타입의 상품 중에서 당일 활성화가 되고 재고가 남은 상품을 필터링한다." {
//        val products: List<ProductEntity> = listOf(
//            ProductEntity(
//                id = UUID.randomUUID().toString(),
//                seller = UserEntity(
//                    id = UUID.randomUUID().toString(),
//                    email = "test",
//                    nickname = "test",
//                    password = "test",
//                    role = Role.SELLER,
//                    isActive = true
//                ),
//                brandEntity = BrandEntity(
//                    id = UUID.randomUUID().toString(),
//                    name = "test",
//                    businessRegistrationNumber = "test",
//                    fullAddress = "test",
//                    ownerUserEntity = UserEntity(
//                        id = UUID.randomUUID().toString(),
//                        email = "test",
//                        nickname = "test",
//                        password = "test",
//                        role = Role.SELLER,
//                        isActive = true
//                    )
//                ),
//                name = "test",
//                price = BigInteger.valueOf(1000),
//                stock = 100,
//                productType = ProductType.FIRST_COME,
//                startDt = LocalDateTime.now().minusDays(1),
//                endDt =LocalDateTime.now().plusDays(1),
//                status = ProductStatus.ACTIVE
//            )
//        )
//        `when`(
//            productRepository.findAllByProductTypeAndStatus(ProductType.FIRST_COME, ProductStatus.ACTIVE)
//        ).thenReturn(products)
//
//        val productIds = productRepository.findAllByProductTypeAndStatus(ProductType.FIRST_COME, ProductStatus.ACTIVE)
//        val activeProduct =
//            productIds.filter { Product.isActiveFirstCome(it)}
//
//        activeProduct.size shouldBe 1
//    }
})
