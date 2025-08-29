package com.nearpick.app.domain.user.controller

import com.nearpick.app.common.response.Response
import com.nearpick.app.common.user.UserPrincipal
import com.nearpick.app.domain.user.dto.CheckEmailRequest
import com.nearpick.app.domain.user.dto.CheckEmailVerificationRequest
import com.nearpick.app.domain.user.dto.CreateUserRequest
import com.nearpick.app.domain.user.dto.UpdateUserRequest
import com.nearpick.app.domain.user.dto.UserResponse
import com.nearpick.app.domain.user.service.UserService
import com.nearpick.app.domain.verification.enum.VerificationType
import com.nearpick.app.domain.verification.service.VerificationService
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val verificationService: VerificationService
) {
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 등록 성공")
    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<Response<UserResponse>> {
        verificationService.isVerifyEmail(VerificationType.SIGNUP_EMAIL, request.email)
        val user = userService.createUser(request)

        return ResponseEntity.ok(Response.success(user))
    }

    @Operation(summary = "사용자 조회", description = "ID로 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 조회 성공")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<Response<UserResponse>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(Response.success(user))
    }

    @Operation(summary = "이메일 인증 번호 전송", description = "이메일 인증 번호를 전송합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 인증 번호 전송 성공")
    @PostMapping("/email")
    fun checkEmail(@RequestBody request: CheckEmailRequest): ResponseEntity<Boolean> {
        userService.checkEmail(request.email)
        verificationService.sendVerificationCode(request.type, request.email, "[nearpick] 이메일 인증")

        return ResponseEntity.ok(true)
    }

    @Operation(summary = "이메일 인증 번호 확인", description = "이메일 인증 번호를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 인증 확인 성공")
    @PostMapping("/email/verification")
    fun checkEmailVerification(@RequestBody request: CheckEmailVerificationRequest): ResponseEntity<Boolean> {
        return ResponseEntity.ok(
            verificationService.verifyCode(request.type, request.email, request.token)
        )
    }

    @Operation(summary = "닉네임 형식 및 중복 확인", description = "닉네임 형식 및 중복을 확인합니다.")
    @ApiResponse(responseCode = "200", description = "닉네임 형식 및 중복 확인 성공")
    @GetMapping("/nickname")
    fun checkNickname(@RequestParam nickname: String): ResponseEntity<Boolean> {
        userService.checkNickname(nickname)

        return ResponseEntity.ok(true)
    }

    @Operation(summary = "사용자 수정", description = "사용자 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 수정 성공")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Response<UserResponse>> {
        val newEmail = request.email
        if (!newEmail.isNullOrBlank()) {
            verificationService.isVerifyEmail(VerificationType.UPDATE_USER_EMAIL, newEmail)
        }

        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(Response.success(user))
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제 처리합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 삭제 성공")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
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
        val user = userService.getUserById(userPrincipal.getUserId())
        return ResponseEntity.ok(Response.success(user))
    }
}
