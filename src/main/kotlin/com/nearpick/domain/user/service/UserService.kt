package com.nearpick.domain.user.service

import com.nearpick.domain.user.dto.UserResponse
import com.nearpick.domain.user.dto.UserSaveRequest
import com.nearpick.domain.user.dto.UserUpdateRequest
import com.nearpick.domain.user.exception.EmailAlreadyExistsException
import com.nearpick.domain.user.exception.UserNotFoundException
import com.nearpick.domain.user.mapper.toEntity
import com.nearpick.domain.user.mapper.toResponse
import com.nearpick.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun createUser(request: UserSaveRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException(request.email)
        }

        val encryptedPassword = passwordEncoder.encode(request.password)
        val user = request.toEntity(encryptedPassword)

        return userRepository.save(user).toResponse()
    }

    fun getUserById(id: String): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }

        return user.toResponse()
    }

    fun updateUser(id: String, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }

        request.nickname?.let { user.nickname = it }
        request.profileImageUrl?.let { user.profileImageUrl = it }
        request.phoneNumber?.let { user.phoneNumber = it }

        return userRepository.save(user).toResponse()
    }

    fun deleteUser(id: String) {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }

        user.isActive = false
        userRepository.save(user)
    }
}
