package com.nearpick.domain.user.exception

import com.nearpick.common.exception.BaseException
import org.springframework.http.HttpStatus

class UserNotFoundException(id: String) : BaseException(
    code = "USER_NOT_FOUND",
    message = "사용자(id=$id)를 찾을 수 없습니다.",
    status = HttpStatus.NOT_FOUND
)
