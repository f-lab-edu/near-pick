package com.nearpick.app.common.security

import com.nearpick.app.domain.auth.dto.JwtToken
import com.nearpick.app.domain.auth.port.JwtTokenProvider
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProviderAdapter(
    private val jwtProperties: JwtProperties
) : JwtTokenProvider {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    override fun generateToken(userId: String, role: String): JwtToken {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiration)

        val jwt = Jwts.builder()
            .setSubject(userId)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()

        return JwtToken(
            accessToken = jwt,
            accessTokenExpiresAt = expiry.toInstant()
        )
    }

    fun getUserId(token: String): String =
        parseClaims(token).subject

    fun getRole(token: String): String =
        parseClaims(token).get("role", String::class.java)

    override fun validateToken(token: String): Boolean = try {
        parseClaims(token)
        true
    } catch (e: Exception) {
        false
    }

    private fun parseClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
}
