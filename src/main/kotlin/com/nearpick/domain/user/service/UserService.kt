package com.nearpick.domain.user.service

import com.nearpick.common.constant.Role
import com.nearpick.common.exception.EmailAlreadyExistsException
import com.nearpick.common.exception.InvalidFormatEmailException
import com.nearpick.common.exception.InvalidFormatNicknameException
import com.nearpick.common.exception.InvalidRoleException
import com.nearpick.common.exception.NicknameAlreadyExistsException
import com.nearpick.common.exception.UserNotFoundException
import com.nearpick.common.validator.Validator
import com.nearpick.domain.user.dto.CreateUserRequest
import com.nearpick.domain.user.dto.UpdateUserRequest
import com.nearpick.domain.user.dto.UserResponse
import com.nearpick.domain.user.entity.User
import com.nearpick.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun createUser(request: CreateUserRequest): UserResponse {
        checkEmail(request.email)
        checkNickname(request.nickname)

        val parsedRole = Role.from(request.role)
        if (parsedRole == Role.ADMIN) {
            throw InvalidRoleException("ADMIN은 직접 등록할 수 없습니다.")
        }

        val encryptedPassword = passwordEncoder.encode(request.password)

        val createdUser = userRepository.save(User.toEntity(request, parsedRole, encryptedPassword))
        return User.toResponse(createdUser)
    }

    fun checkEmail(email: String) {
        if (Validator.isValidEmail(email)) {
            throw InvalidFormatEmailException(email)
        }
        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }
    }

    fun checkNickname(nickname: String) {
        if (!Validator.isValidNickname(nickname)) {
            throw InvalidFormatNicknameException(nickname)
        }
        if (userRepository.existsByNickname(nickname)) {
            throw NicknameAlreadyExistsException(nickname)
        }
    }

    fun getUserById(id: String): UserResponse {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        return User.toResponse(user)
    }

    fun updateUser(id: String, request: UpdateUserRequest): UserResponse {
        if (!request.email.isNullOrBlank()) { checkEmail(request.email) }
        if (!request.nickname.isNullOrBlank()) { checkNickname(request.nickname) }

        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        user.apply {
            email = request.email ?: email
            nickname = request.nickname ?: nickname
            profileImageUrl = request.profileImageUrl ?: profileImageUrl
            phoneNumber = request.phoneNumber ?: phoneNumber
            accountHolderName = request.accountHolderName ?: accountHolderName
            bankName = request.bankName ?: bankName
            accountNumber = request.accountNumber ?: accountNumber
        }

        val updatedUser = userRepository.save(user)
        return User.toResponse(updatedUser)
    }

    fun deleteUser(id: String) {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        user.isActive = false
        userRepository.save(user)
    }
}
