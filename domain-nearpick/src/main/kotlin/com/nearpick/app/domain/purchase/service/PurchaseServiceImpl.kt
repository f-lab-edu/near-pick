package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.InvalidRoleException
import com.nearpick.app.common.exception.PurchaseNotFoundException
import com.nearpick.app.common.exception.ProductNotFoundException
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.mapper.ProductMapper
import com.nearpick.app.domain.product.mapper.ProductResponseMapper
import com.nearpick.app.domain.product.repository.ProductRepository
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailResponse
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseStatusRequest
import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import com.nearpick.app.domain.purchase.entity.ProcessedEventEntity
import com.nearpick.app.domain.purchase.mapper.PurchaseMapper
import com.nearpick.app.domain.purchase.mapper.PurchaseResponseMapper
import com.nearpick.app.domain.purchase.repository.ProcessedEventRepository
import com.nearpick.app.domain.purchase.repository.PurchaseRepository
import com.nearpick.app.domain.stock.service.StockService
import com.nearpick.app.domain.user.mapper.UserMapper
import com.nearpick.app.domain.user.mapper.UserResponseMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = false)
open class PurchaseServiceImpl(
    private val purchaseRepository: PurchaseRepository,
    private val productRepository: ProductRepository,
    private val purchaseMapper: PurchaseMapper,
    private val purchaseResponseMapper: PurchaseResponseMapper,
    private val productMapper: ProductMapper,
    private val productResponseMapper: ProductResponseMapper,
    private val userMapper: UserMapper,
    private val userResponseMapper: UserResponseMapper,
    private val strategyFactory: PurchaseStrategyFactory,
    private val stockService: StockService,
    private val processedEventRepository: ProcessedEventRepository
) : PurchaseService {

    private val log = LoggerFactory.getLogger(javaClass)
    override fun createPurchase(request: CreatePurchaseRequest, userId: String)
        : OrderReceivedResponse {
        val strategy = strategyFactory.getStrategy(request.productType)
        return strategy.createPurchase(request, userId)
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
        val entity = purchaseRepository.findById(id).orElse(null)
            ?: throw PurchaseNotFoundException(id, userId)

        val purchase = purchaseMapper.toDomain(entity)
        val product = productMapper.toDomain(entity.product)

        purchase.verifyAccess(userId, userRole, product.sellerId)
        purchase.updateStatus(userRole, request.status)

        val updatedEntity = purchaseMapper.toEntity(purchase)
        purchaseRepository.save(updatedEntity)

        return purchaseResponseMapper.toResponse(purchase)
    }

    override fun applyPurchase(eventId: String, event: CreatePurchaseRequest, userId: String): Boolean {
        if (processedEventRepository.existsById(eventId)) {
            log.info("[applyPurchase] 이미 처리된 이벤트 무시. eventId={}", eventId)
            return true
        }

        val productEntity = productRepository.findById(event.productId)
            .orElseThrow { ProductNotFoundException(event.productId, userId) }

        val stock = productEntity.stock ?: 0
        if (stock < event.quantity) {
            handleStockInconsistency(event, userId)
            processedEventRepository.save(ProcessedEventEntity(eventId))
            return false
        }

        productEntity.stock = stock - event.quantity
        if (stock == event.quantity) {
            productEntity.status = ProductStatus.INACTIVE_SOLD_OUT
        }
        productRepository.save(productEntity)

        val purchase = Purchase.create(
            userId = userId,
            productId = event.productId,
            productType = event.productType,
            price = event.price,
            quantity = event.quantity,
            reservationDt = event.reservationDt,
            message = event.requestMessage
        )
        purchaseRepository.save(purchaseMapper.toEntity(purchase))

        processedEventRepository.save(ProcessedEventEntity(eventId))
        return true
    }

    private fun handleStockInconsistency(event: CreatePurchaseRequest, userId: String) {
        stockService.recoverStock(event.productId, event.quantity)

        val purchase = Purchase.create(
            userId = userId,
            productId = event.productId,
            productType = event.productType,
            price = event.price,
            quantity = event.quantity,
            reservationDt = event.reservationDt,
            message = event.requestMessage
        )
        purchase.status = PurchaseStatus.CANCELLED
        purchaseRepository.save(purchaseMapper.toEntity(purchase))
    }
}
