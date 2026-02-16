package com.nearpick.app.domain.stock.service

import com.nearpick.app.domain.product.dto.ProductFirstComeCache

data class ReconciliationResult(val checked: Int, val corrected: Int)

interface StockService {
    fun getStockVersionKey(productId: String): String
    fun getProductVersionKey(productId: String): String
    fun initializeDailyStock()
    fun getProductCache(productId: String): ProductFirstComeCache
    fun getStock(productId: String): Int
    fun decreaseStockIfAvailable(productId: String, quantity: Int): Boolean
    fun recoverStock(productId: String, quantity: Int)
    fun reconcileAll(): ReconciliationResult
    fun isDegradedMode(): Boolean
}
