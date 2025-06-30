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
