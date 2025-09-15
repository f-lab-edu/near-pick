package com.nearpick.app.domain.auth.dto

import java.time.Instant

data class JwtToken (
    val accessToken: String,
    val accessTokenExpiresAt: Instant
)
