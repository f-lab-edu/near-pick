package com.nearpick.app.domain.user.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.EmailAlreadyExistsException
import com.nearpick.app.common.exception.InvalidRoleException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.domain.user.dto.CreateUserRequest
import com.nearpick.app.domain.user.dto.UpdateUserRequest
import com.nearpick.app.domain.user.dto.UserResponse
import com.nearpick.app.domain.user.mapper.toEntity
import com.nearpick.app.domain.user.mapper.toResponse
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {
    override fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException(request.email)
        }

        val parsedRole = Role.from(request.role)
        if (parsedRole == Role.ADMIN) {
            throw InvalidRoleException("ADMIN은 직접 등록할 수 없습니다.")
        }

        val encryptedPassword = passwordEncoder.encode(request.password)

        val user = request.toEntity(parsedRole, encryptedPassword)

        return userRepository.save(user).toResponse()
    }

    override fun getUserById(id: String): UserResponse {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        return user.toResponse()
    }

    override fun updateUser(id: String, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        user.apply {
            nickname = request.nickname ?: nickname
            profileImageUrl = request.profileImageUrl ?: profileImageUrl
            phoneNumber = request.phoneNumber ?: phoneNumber
        }

        return userRepository.save(user).toResponse()
    }

    override fun deleteUser(id: String) {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        user.isActive = false
        userRepository.save(user)
    }
}
