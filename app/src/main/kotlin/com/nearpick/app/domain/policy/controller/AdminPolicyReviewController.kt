package com.nearpick.app.domain.policy.controller

import com.nearpick.app.common.response.Response
import com.nearpick.app.common.security.principal.UserPrincipal
import com.nearpick.app.domain.policy.dto.AdminPolicyDecisionRequest
import com.nearpick.app.domain.policy.dto.PolicyReviewSummary
import com.nearpick.app.domain.policy.enum.PolicyVerdict
import com.nearpick.app.domain.policy.service.AdminPolicyReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/policy-reviews")
class AdminPolicyReviewController(
    private val adminPolicyReviewService: AdminPolicyReviewService
) {

    @Operation(summary = "정책 심사 목록 조회 (ADMIN)", description = "verdict 기준으로 심사 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun listReviews(
        @RequestParam(defaultValue = "NEED_REVIEW") verdict: PolicyVerdict
    ): ResponseEntity<Response<List<PolicyReviewSummary>>> {
        return ResponseEntity.ok(Response.success(adminPolicyReviewService.listReviews(verdict)))
    }

    @Operation(summary = "관리자 수동 판정 (ADMIN)", description = "need_review 상품에 대해 수동으로 최종 판정합니다.")
    @ApiResponse(responseCode = "200", description = "판정 성공")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{reviewId}/decision")
    fun decide(
        @PathVariable reviewId: String,
        @RequestBody request: AdminPolicyDecisionRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<String>> {
        adminPolicyReviewService.decide(reviewId, request, userPrincipal.getUserId())
        return ResponseEntity.ok(Response.success("판정 완료: ${request.verdict}"))
    }
}
