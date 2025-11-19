package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.InvalidRoleException
import com.nearpick.app.common.exception.PurchaseNotFoundException
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailResponse
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseStatusRequest
import com.nearpick.app.domain.purchase.repository.PurchaseRepository
import com.nearpick.app.domain.product.mapper.ProductMapper
import com.nearpick.app.domain.product.mapper.ProductResponseMapper
import com.nearpick.app.domain.purchase.mapper.PurchaseMapper
import com.nearpick.app.domain.purchase.mapper.PurchaseResponseMapper
import com.nearpick.app.domain.user.mapper.UserMapper
import com.nearpick.app.domain.user.mapper.UserResponseMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = false)
open class PurchaseServiceImpl(
    private val purchaseRepository: PurchaseRepository,
    private val purchaseMapper: PurchaseMapper,
    private val purchaseResponseMapper: PurchaseResponseMapper,
    private val productMapper: ProductMapper,
    private val productResponseMapper: ProductResponseMapper,
    private val userMapper: UserMapper,
    private val userResponseMapper: UserResponseMapper
) : PurchaseService {
    override fun createPurchase(request: CreatePurchaseRequest, userId: String)
        : PurchaseResponse {

        //TODO: make Logic
        val purchase = Purchase.create(
            userId,
            request.productId,
            request.productType,
            request.price,
            request.quantity,
            request.reservationDt,
            request.requestMessage
        )

        val entity = purchaseMapper.toEntity(purchase)
        purchaseRepository.save(entity)

        return purchaseResponseMapper.toResponse(purchase)
    }

    @Transactional(readOnly = true)
    override fun findAllPurchaseBySeller(userId: String)
        : List<PurchaseResponse> {
        return purchaseRepository.findAllByProduct_Seller_Id(userId)
            .map { purchaseResponseMapper.toResponse(purchaseMapper.toDomain(it)) }
    }

    @Transactional(readOnly = true)
    override fun findAllPurchaseByUser(userId: String)
        : List<PurchaseResponse> {
        return purchaseRepository.findAllByUserId(userId)
            .map { purchaseResponseMapper.toResponse(purchaseMapper.toDomain(it)) }
    }

    @Transactional(readOnly = true)
    override fun findPurchaseDetailByUser(id: String, userId: String)
        : GetPurchaseDetailResponse {
        val entity = purchaseRepository.findByIdAndUserId(id, userId).orElse(null)
            ?: throw PurchaseNotFoundException(id, userId)

        val purchase = purchaseMapper.toDomain(entity)

        val user = userMapper.toDomain(entity.user)
        val userResponse = userResponseMapper.toResponse(user)

        val product = productMapper.toDomain(entity.product)
        val productResponse = productResponseMapper.toResponse(product)

        return purchaseResponseMapper.toDetailResponseByUser(purchase, userResponse, productResponse)
    }

    @Transactional(readOnly = true)
    override fun findPurchaseDetailBySeller(id: String, userId: String)
        : GetPurchaseDetailResponse {
        val entity = purchaseRepository.findByIdAndProduct_Seller_Id(id, userId).orElse(null)
            ?: throw PurchaseNotFoundException(id, userId)

        val purchase = purchaseMapper.toDomain(entity)

        val user = userMapper.toDomain(entity.user)
        val userResponse = userResponseMapper.toResponse(user)

        val product = productMapper.toDomain(entity.product)
        val productResponse = productResponseMapper.toResponse(product)

        return purchaseResponseMapper.toDetailResponseBySeller(purchase, userResponse, productResponse)
    }

    override fun updatePurchase(id: String, userId: String, request: UpdatePurchaseRequest)
        : PurchaseResponse {
        val entity = purchaseRepository.findByIdAndProduct_Seller_Id(id, userId).orElse(null)
            ?: throw PurchaseNotFoundException(id, userId)
        val purchase = purchaseMapper.toDomain(entity)

        //TODO: 선착순 구매, 예약 기능에 영향을 주므로 개발 이후 구현 예정
//        purchase.update(request)

        val updatedEntity = purchaseMapper.toEntity(purchase)
        purchaseRepository.save(updatedEntity)

        return purchaseResponseMapper.toResponse(purchase)
    }

    override fun updatePurchaseStatus(id: String, userId: String, userRole: Role, request: UpdatePurchaseStatusRequest)
        : PurchaseResponse {
        val entity = when (userRole) {
            Role.SELLER -> purchaseRepository.findByIdAndProduct_Seller_Id(id, userId).orElse(null)
                ?: throw PurchaseNotFoundException(id, userId)
            Role.USER -> purchaseRepository.findByIdAndUserId(id, userId).orElse(null)
                ?: throw PurchaseNotFoundException(id, userId)
            else -> throw InvalidRoleException("Unsupported role: $userRole")
        }
        val purchase = purchaseMapper.toDomain(entity)

        purchase.updateStatus(userRole, request.status)

        val updatedEntity = purchaseMapper.toEntity(purchase)
        purchaseRepository.save(updatedEntity)

        return purchaseResponseMapper.toResponse(purchase)
    }
}
