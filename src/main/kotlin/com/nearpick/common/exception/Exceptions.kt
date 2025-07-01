package com.nearpick.common.exception

import org.springframework.http.HttpStatus

// 사용자 관련 예외
class UserNotFoundException(userId: String) : BaseException(
    code = "USER_NOT_FOUND",
    message = "해당 사용자를 찾을 수 없습니다. (userId=$userId)",
    status = HttpStatus.NOT_FOUND
)

class EmailAlreadyExistsException(email: String) : BaseException(
    code = "EMAIL_ALREADY_EXISTS",
    message = "이미 사용 중인 이메일입니다. (email=$email)",
    status = HttpStatus.CONFLICT
)

class InvalidRoleException(role: String?) : BaseException(
    code = "INVALID_ROLE",
    message = "올바르지 않은 Role입니다. (role = $role)",
    status = HttpStatus.BAD_REQUEST
)

// 인증 관련 예외
class InvalidEmailException() : BaseException(
    code = "INVALID_EMAIL",
    message = "이메일이 일치하지 않습니다.",
    status = HttpStatus.UNAUTHORIZED
)

class InvalidPasswordException() : BaseException(
    code = "INVALID_PASSWORD",
    message = "비밀번호가 일치하지 않습니다.",
    status = HttpStatus.UNAUTHORIZED
)
