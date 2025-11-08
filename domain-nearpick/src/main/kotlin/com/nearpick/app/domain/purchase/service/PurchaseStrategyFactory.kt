package com.nearpick.app.domain.purchase.service

import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.purchase.service.createStrategy.FirstComePurchaseStrategy
import com.nearpick.app.domain.purchase.service.createStrategy.NormalPurchaseStrategy
import com.nearpick.app.domain.purchase.service.createStrategy.PurchaseStrategy
import com.nearpick.app.domain.purchase.service.createStrategy.ReservationPurchaseStrategy
import org.springframework.stereotype.Service

@Service
class PurchaseStrategyFactory(
    private val normalPurchaseStrategy: NormalPurchaseStrategy,
    private val firstComePurchaseStrategy: FirstComePurchaseStrategy,
    private val reservationPurchaseStrategy: ReservationPurchaseStrategy
) {
    fun getStrategy(type: ProductType): PurchaseStrategy = when (type) {
        ProductType.NORMAL -> normalPurchaseStrategy
        ProductType.FIRST_COME -> firstComePurchaseStrategy
        ProductType.RESERVATION -> reservationPurchaseStrategy
    }
}
