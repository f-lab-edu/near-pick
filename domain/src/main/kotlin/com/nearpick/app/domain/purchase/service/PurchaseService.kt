package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailBySellerResponse
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailByUserResponse
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseStatusRequest

interface PurchaseService {
    fun createPurchase(request: CreatePurchaseRequest, userId: String): OrderReceivedResponse
    fun findAllPurchaseBySeller(userId: String): List<PurchaseResponse>
    fun findAllPurchaseByUser(userId: String): List<PurchaseResponse>
    fun findPurchaseDetailByUser(id: String, userId: String): GetPurchaseDetailByUserResponse
    fun findPurchaseDetailBySeller(id: String, userId: String): GetPurchaseDetailBySellerResponse
    fun updatePurchase(id: String, userId: String, request: UpdatePurchaseRequest): PurchaseResponse
    fun updatePurchaseStatus(id: String, userId: String, userRole: Role, request: UpdatePurchaseStatusRequest): PurchaseResponse
    fun applyPurchase(event: CreatePurchaseRequest, userId: String): Boolean
}
