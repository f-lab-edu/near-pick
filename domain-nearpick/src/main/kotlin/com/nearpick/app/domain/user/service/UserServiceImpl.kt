package com.nearpick.app.domain.user.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.EmailAlreadyExistsException
import com.nearpick.app.common.exception.InvalidFormatEmailException
import com.nearpick.app.common.exception.InvalidRoleException
import com.nearpick.app.common.exception.UserNotFoundException
import com.nearpick.app.common.exception.InvalidFormatNicknameException
import com.nearpick.app.common.exception.NicknameAlreadyExistsException
import com.nearpick.app.common.validator.Validator
import com.nearpick.app.domain.auth.port.PasswordMatcher
import com.nearpick.app.domain.user.dto.CreateUserRequest
import com.nearpick.app.domain.user.dto.UpdateUserRequest
import com.nearpick.app.domain.user.dto.UserPrincipalResponse
import com.nearpick.app.domain.user.dto.UserResponse
import com.nearpick.app.domain.user.mapper.UserMapper
import com.nearpick.app.domain.user.mapper.UserResponseMapper
import com.nearpick.app.domain.user.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = false)
open class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordMatcher: PasswordMatcher,
    private val userMapper: UserMapper,
    private val userResponseMapper: UserResponseMapper,
) : UserService {
    override fun loadUserByUsername(id: String): UserPrincipalResponse {
        val entity = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        val user = userMapper.toDomain(entity)

        return userResponseMapper.toPrincipalResponse(user)

    }

    override fun createUser(request: CreateUserRequest): UserResponse {
        val parsedRole = Role.from(request.role)
        if (parsedRole == Role.ADMIN) {
            throw InvalidRoleException("ADMIN은 직접 등록할 수 없습니다.")
        }

        val encryptedPassword = passwordMatcher.encode(request.password)

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

        val entity = userMapper.toEntity(user)
        userRepository.save(entity)

        val savedUser = userMapper.toDomain(entity)
        return userResponseMapper.toResponse(savedUser)
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
        val entity = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        val user = userMapper.toDomain(entity)
        return userResponseMapper.toResponse(user)
    }

    override fun updateUser(id: String, request: UpdateUserRequest): UserResponse {
        val newEmail = request.email
        if (!newEmail.isNullOrBlank()) {
            checkEmail(newEmail)
        }

        val newNickname = request.nickname
        if (!newNickname.isNullOrBlank()) {
            checkNickname(newNickname)
        }

        val entity = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }
        val user = userMapper.toDomain(entity)

        user.update(
            newEmail,
            newNickname,
            request.profileImageUrl,
            request.phoneNumber,
            request.accountHolderName,
            request.bankName,
            request.accountNumber
        )

        val updatedEntity = userMapper.toEntity(user)
        userRepository.save(updatedEntity)

        return userResponseMapper.toResponse(user)
    }

    override fun deleteUser(id: String) {
        val user = userRepository.findById(id).getOrElse { throw UserNotFoundException(id) }

        user.isActive = false
        userRepository.save(user)
    }
}
