package com.nearpick.app.domain.user.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.EmailAlreadyExistsException
import com.nearpick.app.common.exception.InvalidFormatEmailException
import com.nearpick.app.common.exception.InvalidRoleException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.common.exception.InvalidFormatNicknameException
import com.nearpick.app.common.exception.NicknameAlreadyExistsException
import com.nearpick.app.common.validator.Validator
import com.nearpick.app.domain.user.dto.CreateUserRequest
import com.nearpick.app.domain.user.dto.UpdateUserRequest
import com.nearpick.app.domain.user.dto.UserResponse
import com.nearpick.app.domain.user.entity.UserEntity
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = false)
open class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun createUser(request: CreateUserRequest): UserResponse {
        val parsedRole = Role.from(request.role)
        if (parsedRole == Role.ADMIN) {
            throw InvalidRoleException("ADMIN은 직접 등록할 수 없습니다.")
        }

        val encryptedPassword = passwordEncoder.encode(request.password)

        val user = User(
            email = request.email,
            nickname = request.nickname,
            password = encryptedPassword,
            profileImageUrl = request.profileImageUrl,
            phoneNumber = request.phoneNumber,
            role = parsedRole,
            accountHolderName = request.accountHolderName,
            bankName = request.bankName,
            accountNumber = request.accountNumber
        )

        checkEmail(request.email)
        checkNickname(request.nickname)

        val createdUserEntity = user.toEntity()
        return User.toResponse(userRepository.save(createdUserEntity))
    }

    @Transactional(readOnly = true)
    override fun checkEmail(email: String) {
        if (Validator.isValidEmail(email)) {
            throw InvalidFormatEmailException(email)
        }
        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }
    }

    @Transactional(readOnly = true)
    override fun checkNickname(nickname: String) {
        if (!Validator.isValidNickname(nickname)) {
            throw InvalidFormatNicknameException(nickname)
        }
        if (userRepository.existsByNickname(nickname)) {
            throw NicknameAlreadyExistsException(nickname)
        }
    }

    @Transactional(readOnly = true)
    override fun getUserById(id: String): UserResponse {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        return User.toResponse(user)
    }

    override fun updateUser(id: String, request: UpdateUserRequest): UserResponse {
        val newEmail = request.email
        if (!newEmail.isNullOrBlank()) { checkEmail(newEmail) }

        val newNickname = request.nickname
        if (!newNickname.isNullOrBlank()) { checkNickname(newNickname) }

        val userEntity = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        val user = User.from(userEntity)

        user.update(request)

        return User.toResponse(userRepository.save(user.toEntity()))
    }

    override fun deleteUser(id: String) {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        user.isActive = false
        userRepository.save(user)
    }
}
