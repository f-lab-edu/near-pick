package com.nearpick.app.domain.purchase.controller

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.InvalidRoleException
import com.nearpick.app.common.response.Response
import com.nearpick.app.common.security.principal.UserPrincipal
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.PurchaseResponse
import com.nearpick.app.domain.purchase.service.PurchaseService
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailBySellerResponse
import com.nearpick.app.domain.purchase.dto.GetPurchaseDetailResponse
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
import javax.management.relation.RoleNotFoundException

@RestController
@RequestMapping("/api/v1/purchases")
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

//    @Operation(summary = "판매자 별 주문 조회", description = "해당 판매자가 등록한 상품을 주문한 전체 내역을 조회합니다.")
//    @ApiResponse(responseCode = "200", description = "판매자의 주문 조회 성공")
//    @GetMapping("/seller")
//    @PreAuthorize("hasRole('SELLER')")
//    fun getPurchaseListBySeller(
//        @AuthenticationPrincipal userPrincipal: UserPrincipal
//    ): ResponseEntity<Response<List<PurchaseResponse>>> {
//        return ResponseEntity.ok(
//            Response.success(
//                purchaseService.findAllPurchaseBySeller(userPrincipal.getUserId())
//            )
//        )
//    }

    @Operation(summary = "주문 조회", description = "해당 판매자가 등록한 상품을 주문한 내역이나 해당 사용자가 직접 주문한 전체 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자의 주문 조회 성공")
    @GetMapping()
    @PreAuthorize("hasRole('SELLER') || hasRole('USER')")
    fun getPurchaseListByUser(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<List<PurchaseResponse>>> {

        val purchases = when {
            userPrincipal.getRole().equals(Role.SELLER) -> purchaseService.findAllPurchaseBySeller(userPrincipal.getUserId())
            userPrincipal.getRole().equals(Role.USER) -> purchaseService.findAllPurchaseByUser(userPrincipal.getUserId())
            else -> throw InvalidRoleException(userPrincipal.getRole().name)
        }
        return ResponseEntity.ok(
            Response.success(
                purchases
            )
        )
    }

    @Operation(summary = "주문 상세 조회", description = "주문을 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') || hasRole('USER')")
    fun getPurchaseDetailByUser(
        @PathVariable id: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<GetPurchaseDetailResponse>> {
        val purchase = when {
            userPrincipal.getRole().equals(Role.SELLER) -> purchaseService.findPurchaseDetailBySeller(id, userPrincipal.getUserId())
            userPrincipal.getRole().equals(Role.USER) -> purchaseService.findPurchaseDetailByUser(id, userPrincipal.getUserId())
            else -> throw InvalidRoleException(userPrincipal.getRole().name)
        }

        return ResponseEntity.ok(
            Response.success(
                purchase
            )
        )
    }

//    @Operation(summary = "판매자의 주문을 상세 조회", description = "판매자의 주문을 상세 조회합니다.")
//    @ApiResponse(responseCode = "200", description = "판매자의 주문 상세 조회 성공")
//    @GetMapping("/seller/{id}")
//    @PreAuthorize("hasRole('SELLER')")
//    fun getPurchaseDetailBySeller(
//        @PathVariable id: String,
//        @AuthenticationPrincipal userPrincipal: UserPrincipal
//    ): ResponseEntity<Response<GetPurchaseDetailBySellerResponse>> {
//        return ResponseEntity.ok(
//            Response.success(
//                purchaseService.findPurchaseDetailBySeller(id, userPrincipal.getUserId())
//            )
//        )
//    }

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
