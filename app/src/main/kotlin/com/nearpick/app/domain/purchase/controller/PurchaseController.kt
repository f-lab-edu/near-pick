package com.nearpick.app.domain.purchase.controller

import com.nearpick.app.common.response.Response
import com.nearpick.app.common.security.principal.UserPrincipal
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.service.PurchaseService
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailBySellerResponse
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailByUserResponse
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseStatusRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/purchase")
class PurchaseController(
    private val purchaseService: PurchaseService
) {
    @Operation(summary = "주문 주문(USER)", description = "판매자의 주문을 구매합니다.(USER)")
    @ApiResponse(responseCode = "200", description = "판매자 주문 구매 성공")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    fun createPurchase(
        @RequestBody request: CreatePurchaseRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<PurchaseResponse>> {
        return ResponseEntity.ok(
            Response.success(
                purchaseService.createPurchase(request, userPrincipal.getUserId())
            )
        )
    }

    @Operation(summary = "판매자 별 주문 조회", description = "판매자 기준으로 주문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "판매자의 주문 조회 성공")
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    fun getPurchaseListBySeller(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<List<PurchaseResponse>>> {
        return ResponseEntity.ok(
            Response.success(
                purchaseService.findAllPurchaseBySeller(userPrincipal.getUserId())
            )
        )
    }

    @Operation(summary = "사용자 별 주문 조회", description = "사용자 기준으로 주문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자의 주문 조회 성공")
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    fun getPurchaseListByUser(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<List<PurchaseResponse>>> {
        return ResponseEntity.ok(
            Response.success(
                purchaseService.findAllPurchaseByUser(userPrincipal.getUserId())
            )
        )
    }

    @Operation(summary = "사용자의 주문을 상세 조회", description = "사용자의 주문을 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자의 주문 상세 조회 성공")
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('USER')")
    fun getPurchaseDetailByUser(
        @PathVariable id: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<GetPurchaseDetailByUserResponse>> {
        return ResponseEntity.ok(
            Response.success(
                purchaseService.findPurchaseDetailByUser(id, userPrincipal.getUserId())
            )
        )
    }

    @Operation(summary = "판매자의 주문을 상세 조회", description = "판매자의 주문을 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "판매자의 주문 상세 조회 성공")
    @GetMapping("/seller/{id}")
    @PreAuthorize("hasRole('SELLER')")
    fun getPurchaseDetailBySeller(
        @PathVariable id: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<GetPurchaseDetailBySellerResponse>> {
        return ResponseEntity.ok(
            Response.success(
                purchaseService.findPurchaseDetailBySeller(id, userPrincipal.getUserId())
            )
        )
    }

    //TODO: 선착순 구매, 예약 기능에 영향을 주므로 개발 이후 구현 예정
//    @Operation(summary = "판매자 주문 수정(SELLER)", description = "판매자의 주문을 수정합니다.(SELLER)")
//    @ApiResponse(responseCode = "200", description = "판매자 주문 수정 성공")
//    @PreAuthorize("hasRole('SELLER')")
//    @PutMapping("/{id}")
//    fun updatePurchase(
//        @PathVariable id: String,
//        @RequestBody request: UpdatePurchaseRequest,
//        @AuthenticationPrincipal userPrincipal: UserPrincipal
//    ): ResponseEntity<Response<PurchaseResponse>> {
//        return ResponseEntity.ok(
//            Response.success(
//                purchaseService.updatePurchase(id, userPrincipal.getUserId(), request)
//            )
//        )
//    }

    @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "판매자 주문 상태 변경 성공")
    @PutMapping("/{id}/status")
    fun updatePurchaseStatus(
        @PathVariable id: String,
        @RequestBody request: UpdatePurchaseStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<String>> {
        purchaseService.updatePurchaseStatus(id, userPrincipal.getUserId(), userPrincipal.getRole(), request)
        return ResponseEntity.ok(Response.success("success"))
    }
}
