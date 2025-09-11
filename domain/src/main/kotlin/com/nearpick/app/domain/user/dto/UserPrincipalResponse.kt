package com.nearpick.app.domain.user.dto

import com.nearpick.app.common.constant.Role

data class UserPrincipalResponse (
    val id: String,
    val email: String,
    val password: String,
    val role: Role,
    val isActive: Boolean
)
