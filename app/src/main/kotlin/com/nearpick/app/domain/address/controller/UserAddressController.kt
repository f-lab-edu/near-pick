package com.nearpick.app.domain.address.controller

import com.nearpick.app.common.response.Response
import com.nearpick.app.domain.address.dto.CreateUserAddressRequest
import com.nearpick.app.domain.address.dto.UpdateUserAddressRequest
import com.nearpick.app.domain.address.dto.UserAddressResponse
import com.nearpick.app.domain.address.service.UserAddressService
import com.nearpick.app.common.user.UserPrincipal
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
@RequestMapping("/api/v1/user-address")
class UserAddressController(
    private val userAddressService: UserAddressService
) {
    @Operation(summary = "사용자 주소 등록(USER)", description = "사용자의 주소를 등록합니다.(USER)")
    @ApiResponse(responseCode = "200", description = "사용자 주소 등록 성공")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    fun save(
        @RequestBody request: CreateUserAddressRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<UserAddressResponse>> {
        return ResponseEntity.ok(
            Response.success(
                userAddressService.createUserAddress(
                    request,
                    userPrincipal.getUserId()
                )
            )
        )
    }

    @Operation(summary = "사용자 주소 전체 조회(USER)", description = "사용자의 주소를 전체 조회합니다.(USER)")
    @ApiResponse(responseCode = "200", description = "사용자 주소 전체 조회 성공")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    fun list(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<List<UserAddressResponse>>> {
        return ResponseEntity.ok(
            Response.success(userAddressService.findAllUserAddressByUserId(userPrincipal.getUserId()))
        )
    }

    @Operation(summary = "사용자 주소 수정(USER)", description = "사용자의 주소를 수정합니다.(USER)")
    @ApiResponse(responseCode = "200", description = "사용자 주소 수정 성공")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody request: UpdateUserAddressRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<UserAddressResponse>> {
        return ResponseEntity.ok(
            Response.success(
                userAddressService.updateUserAddress(
                    id,
                    userPrincipal.getUserId(),
                    request
                )
            )
        )
    }

    @Operation(summary = "사용자 주소 삭제(USER)", description = "사용자의 주소를 삭제합니다.(USER)")
    @ApiResponse(responseCode = "200", description = "사용자 주소 삭제 성공")
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<String>> {
        userAddressService.deleteUserAddress(id, userPrincipal.getUserId())
        return ResponseEntity.ok(Response.success("success"))
    }
}
