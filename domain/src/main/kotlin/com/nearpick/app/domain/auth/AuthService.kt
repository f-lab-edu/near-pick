package com.nearpick.app.domain.auth

import com.nearpick.app.domain.auth.dto.LoginRequest
import com.nearpick.app.domain.auth.dto.LoginResponse

interface AuthService {
    fun login(request: LoginRequest): LoginResponse
}
