package com.nearpick.app.common.security

import com.nearpick.app.domain.auth.port.PasswordMatcher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordMatcherAdapter(
    private val encoder: PasswordEncoder
) : PasswordMatcher {
    override fun encode(rawPassword: CharSequence) = encoder.encode(rawPassword.toString())
    override fun matches(raw: String, encoded: String) = encoder.matches(raw, encoded)
}
