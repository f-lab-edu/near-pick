package com.nearpick.domain.product.controller

import com.nearpick.common.response.Response
import com.nearpick.domain.auth.dto.UserPrincipal
import com.nearpick.domain.product.dto.CreateProductRequest
import com.nearpick.domain.product.dto.GetProductDetailResponse
import com.nearpick.domain.product.dto.ProductResponse
import com.nearpick.domain.product.dto.UpdateProductRequest
import com.nearpick.domain.product.service.ProductService
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
@RequestMapping("/api/v1/product")
class ProductController(
    private val productService: ProductService
) {
    @Operation(summary = "판매자 상품 등록(SELLER)", description = "판매자의 상품을 등록합니다.(SELLER)")
    @ApiResponse(responseCode = "200", description = "판매자 상품 등록 성공")
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    fun createProduct(
        @RequestBody request: CreateProductRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<ProductResponse>> {
        return ResponseEntity.ok(
            Response.success(
                productService.createProduct(request, userPrincipal.getUser())
            )
        )
    }

    @Operation(summary = "브랜드 별 상품 조회", description = "브랜드 별 상품을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "브랜드 별 상품 조회 성공")
    @GetMapping("brand/{brandId}")
    fun getProductListByBrand(
        @PathVariable brandId: String
    ): ResponseEntity<Response<List<ProductResponse>>> {
        return ResponseEntity.ok(
            Response.success(
                productService.findAllProductByBrand(brandId)
            )
        )
    }

    @Operation(summary = "판매자 상품을 상세 조회", description = "판매자의 상품을 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "판매자 상품 상세 조회 성공")
    @GetMapping("/{id}")
    fun getProductDetail(
        @PathVariable id: String
    ): ResponseEntity<Response<GetProductDetailResponse>> {
        return ResponseEntity.ok(
            Response.success(
                productService.findProductDetail(id)
            )
        )
    }

    @Operation(summary = "판매자 상품 수정(SELLER)", description = "판매자의 상품을 수정합니다.(SELLER)")
    @ApiResponse(responseCode = "200", description = "판매자 상품 수정 성공")
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: String,
        @RequestBody request: UpdateProductRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<ProductResponse>> {
        return ResponseEntity.ok(
            Response.success(
                productService.updateProduct(id, userPrincipal.getUser(), request)
            )
        )
    }

    @Operation(summary = "판매자 상품 삭제(SELLER)", description = "판매자의 상품을 삭제합니다.(SELLER)")
    @ApiResponse(responseCode = "200", description = "판매자 상품 삭제 성공")
    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<String>> {
        productService.deleteProduct(id, userPrincipal.getUser())
        return ResponseEntity.ok(Response.success("success"))
    }
}
