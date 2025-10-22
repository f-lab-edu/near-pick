package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.InvalidRoleException
import com.nearpick.app.common.exception.PurchaseNotFoundException
import com.nearpick.app.common.exception.ProductNotFoundException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailBySellerResponse
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailByUserResponse
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseStatusRequest
import com.nearpick.app.domain.purchase.entity.PurchaseEntity
import com.nearpick.app.domain.purchase.repository.PurchaseRepository
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse


@Service
@Transactional(readOnly = false)
open class PurchaseServiceImpl(
    private val purchaseRepository: PurchaseRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : PurchaseService {
    override fun createPurchase(request: CreatePurchaseRequest, userId: String)
        : PurchaseResponse {
        val productEntity = getProduct(request.productId)
        val userEntity = userRepository.findById(userId).getOrElse { throw UserNotFoundException(userId) }

        //TODO: make Logic
        val purchase = Purchase.create(request, userEntity, productEntity)

        return Purchase.toResponse(purchaseRepository.save(purchase.toEntity()))
    }

    @Transactional(readOnly = true)
    override fun findAllPurchaseBySeller(userId: String)
        : List<PurchaseResponse> {
        return purchaseRepository.findAllByProduct_Seller_Id(userId).map { Purchase.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun findAllPurchaseByUser(userId: String)
        : List<PurchaseResponse> {
        return purchaseRepository.findAllByUserId(userId).map { Purchase.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun findPurchaseDetailByUser(id: String, userId: String)
        : GetPurchaseDetailByUserResponse {
        val purchaseEntity = getPurchaseByUser(id, userId)

        return Purchase.toDetailResponseByUser(purchaseEntity)
    }

    @Transactional(readOnly = true)
    override fun findPurchaseDetailBySeller(id: String, userId: String)
        : GetPurchaseDetailBySellerResponse {
        val purchaseEntity = getPurchaseBySeller(id, userId)

        return Purchase.toDetailResponseBySeller(purchaseEntity)
    }

    override fun updatePurchase(id: String, userId: String, request: UpdatePurchaseRequest)
        : PurchaseResponse {

        val purchaseEntity = getPurchaseBySeller(id, userId)
        val purchase = Purchase.from(purchaseEntity)

        //TODO: 선착순 구매, 예약 기능에 영향을 주므로 개발 이후 구현 예정
//        purchase.update(request)

        return Purchase.toResponse(purchaseRepository.save(purchase.toEntity()))
    }

    override fun updatePurchaseStatus(id: String, userId: String, userRole: Role, request: UpdatePurchaseStatusRequest)
        : PurchaseResponse {
        val purchaseEntity = when (userRole) {
            Role.SELLER -> getPurchaseByUser(id, userId)
            Role.USER -> getPurchaseByUser(id, userId)
            else -> throw InvalidRoleException("Unsupported role: $userRole")
        }
        val purchase = Purchase.from(purchaseEntity)

        purchase.updateStatus(userRole, request.status)

        return Purchase.toResponse(purchaseRepository.save(purchase.toEntity()))
    }

    private fun getProduct(id: String): ProductEntity =
        productRepository.findById(id).orElse(null)
            ?: throw ProductNotFoundException(id, null)

    private fun getPurchaseBySeller(id: String, userId: String): PurchaseEntity =
        purchaseRepository.findByIdAndProduct_Seller_Id(id, userId).orElse(null)
            ?: throw PurchaseNotFoundException(id, userId)

    private fun getPurchaseByUser(id: String, userId: String): PurchaseEntity =
        purchaseRepository.findByIdAndUserId(id, userId).orElse(null)
            ?: throw PurchaseNotFoundException(id, userId)
}
