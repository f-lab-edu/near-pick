package com.nearpick.domain.user.exception

import com.nearpick.common.exception.BaseException
import org.springframework.http.HttpStatus

class EmailAlreadyExistsException(email: String) : BaseException(
    code = "EMAIL_ALREADY_EXISTS",
    message = "이미 사용 중인 이메일입니다. (email=$email)",
    status = HttpStatus.CONFLICT
)
