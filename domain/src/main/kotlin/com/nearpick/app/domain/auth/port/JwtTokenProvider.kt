package com.nearpick.app.domain.auth.port

import com.nearpick.app.domain.auth.dto.JwtToken

interface JwtTokenProvider {
    fun generateToken(userId: String, role: String): JwtToken
    fun validateToken(token: String): Boolean
}
