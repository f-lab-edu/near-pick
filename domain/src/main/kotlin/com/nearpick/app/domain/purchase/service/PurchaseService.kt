package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailResponse
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseStatusRequest

interface PurchaseService {
    fun createPurchase(request: CreatePurchaseRequest, userId: String): OrderReceivedResponse
    fun findAllPurchaseBySeller(userId: String): List<PurchaseResponse>
    fun findAllPurchaseByUser(userId: String): List<PurchaseResponse>
    fun findPurchaseDetailByUser(id: String, userId: String): GetPurchaseDetailResponse
    fun findPurchaseDetailBySeller(id: String, userId: String): GetPurchaseDetailResponse
    fun updatePurchase(id: String, userId: String, request: UpdatePurchaseRequest): PurchaseResponse
    fun updatePurchaseStatus(id: String, userId: String, userRole: Role, request: UpdatePurchaseStatusRequest): PurchaseResponse
    fun applyPurchase(eventId: String, event: CreatePurchaseRequest, userId: String): Boolean
}
