package com.nearpick.domain.user.controller

import com.nearpick.common.response.Response
import com.nearpick.domain.auth.dto.UserPrincipal
import com.nearpick.domain.user.dto.CreateUserRequest
import com.nearpick.domain.user.dto.UpdateUserRequest
import com.nearpick.domain.user.dto.UserResponse
import com.nearpick.domain.user.mapper.toResponse
import com.nearpick.domain.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
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
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 등록 성공")
    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<Response<UserResponse>> {
        val user = userService.createUser(request)
        return ResponseEntity.ok(Response.success(user))
    }

    @Operation(summary = "사용자 수정", description = "사용자 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 수정 성공")
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<Response<UserResponse>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(Response.success(user))
    }

    @Operation(summary = "사용자 조회", description = "ID로 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 조회 성공")
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Response<UserResponse>> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(Response.success(user))
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제 처리합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 삭제 성공")
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<Response<String>> {
        userService.deleteUser(id)
        return ResponseEntity.ok(Response.success("success"))
    }

    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 정보 조회 성공")
    @GetMapping("/me")
    fun getMyInfo(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Response<UserResponse>> {
        return ResponseEntity.ok(Response.success(userPrincipal.getUser().toResponse()))
    }
}
