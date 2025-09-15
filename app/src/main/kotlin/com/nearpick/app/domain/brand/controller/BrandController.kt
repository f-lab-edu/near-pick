package com.nearpick.app.domain.brand.controller

import com.nearpick.app.common.response.Response
import com.nearpick.app.common.security.principal.UserPrincipal
import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.CreateBrandRequest
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.brand.dto.UpdateBrandRequest
import com.nearpick.app.domain.brand.service.BrandService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/brand")
class BrandController(
    private val brandService: BrandService
) {
    @Operation(summary = "판매자 가게 등록(SELLER)", description = "판매자의 가게를 등록합니다.(SELLER)")
    @ApiResponse(responseCode = "200", description = "판매자 가게 등록 성공")
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    fun createBrand(
        @RequestBody request: CreateBrandRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<BrandResponse>> {
        return ResponseEntity.ok(
            Response.success(
                brandService.createBrand(request, userPrincipal.getUserId())
            )
        )
    }

    @Operation(summary = "판매자 가게 전체 조회(SELLER)", description = "판매자의 가게를 전체 조회합니다.(SELLER)")
    @ApiResponse(responseCode = "200", description = "판매자 가게 전체 조회 성공")
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping
    fun getBrandListByUser(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<List<BrandResponse>>> {
        return ResponseEntity.ok(
            Response.success(
                brandService.findAllBrandByOwnerUser(userPrincipal.getUserId())
            )
        )
    }

    @Operation(summary = "판매자 가게를 상세 조회", description = "판매자의 가게를 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "판매자 가게 상세 조회 성공")
    @GetMapping("/{id}")
    fun getBrandDetail(
        @PathVariable id: String
    ): ResponseEntity<Response<GetBrandDetailResponse>> {
        return ResponseEntity.ok(
            Response.success(
                brandService.findBrandDetail(id)
            )
        )
    }

    @Operation(summary = "판매자 가게 수정(SELLER)", description = "판매자의 가게를 수정합니다.(SELLER)")
    @ApiResponse(responseCode = "200", description = "판매자 가게 수정 성공")
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{id}")
    fun updateBrand(
        @PathVariable id: String,
        @RequestBody request: UpdateBrandRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<BrandResponse>> {
        return ResponseEntity.ok(
            Response.success(
                brandService.updateBrand(id, userPrincipal.getUserId(), request)
            )
        )
    }

    @Operation(summary = "판매자 가게 삭제(SELLER)", description = "판매자의 가게를 삭제합니다.(SELLER)")
    @ApiResponse(responseCode = "200", description = "판매자 가게 삭제 성공")
    @DeleteMapping("/{id}")
    fun deleteBrand(
        @PathVariable id: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<String>> {
        brandService.deleteBrand(id, userPrincipal.getUserId())
        return ResponseEntity.ok(Response.success("success"))
    }
}
