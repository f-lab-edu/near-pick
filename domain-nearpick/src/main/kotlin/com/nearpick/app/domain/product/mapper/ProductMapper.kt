package com.nearpick.app.domain.product.mapper

import com.nearpick.app.domain.brand.repository.BrandRepository
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.service.Product
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ProductMapper(
    private val userRepository: UserRepository,
    private val brandRepository: BrandRepository
) {

    fun toEntity(product: Product): ProductEntity {
        val sellerProxy = userRepository.getReferenceById(product.sellerId)
        val brandProxy = brandRepository.getReferenceById(product.brandId)

        return ProductEntity(
            id = product.id ?: UUID.randomUUID().toString(),
            seller = sellerProxy,
            brandEntity = brandProxy,
            name = product.name,
            description = product.description,
            price = product.price,
            stock = product.stock,
            productType = product.productType,
            startDt = product.startDt,
            endDt = product.endDt,
            status = product.status ?: ProductStatus.PENDING
        )
    }

    fun toDomain(productEntity: ProductEntity): Product {
        return Product(
            id = productEntity.id,
            sellerId = productEntity.seller.id,
            brandId = productEntity.brandEntity.id,
            name = productEntity.name,
            description = productEntity.description,
            price = productEntity.price,
            stock = productEntity.stock,
            productType = productEntity.productType,
            startDt = productEntity.startDt,
            endDt = productEntity.endDt,
            status = productEntity.status
        )
    }
}
