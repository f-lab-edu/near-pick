package com.nearpick.app.domain.auth.port

interface PasswordMatcher {
    fun encode(rawPassword: CharSequence): String
    fun matches(raw: String, encoded: String): Boolean
}
