package com.nearpick.domain.user.controller

import com.nearpick.common.response.ApiResponse
import com.nearpick.domain.user.dto.UserResponse
import com.nearpick.domain.user.dto.UserSaveRequest
import com.nearpick.domain.user.dto.UserUpdateRequest
import com.nearpick.domain.user.service.UserService
import org.springframework.http.ResponseEntity
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

    @PostMapping
    fun createUser(@RequestBody request: UserSaveRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val response = userService.createUser(request)
        return ResponseEntity.ok(ApiResponse(data = response))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<ApiResponse<UserResponse>> {
        val response = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse(data = response))
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String,
        @RequestBody request: UserUpdateRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val response = userService.updateUser(id, request)
        return ResponseEntity.ok(ApiResponse(data = response))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<ApiResponse<String>> {
        userService.deleteUser(id)
        return ResponseEntity.ok(ApiResponse(data = "success"))
    }
}
