package com.nearpick.common.security

import com.nearpick.common.security.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateToken(userId: String, role: String): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts.builder()
            .setSubject(userId)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getUserId(token: String): String =
        parseClaims(token).subject

    fun getRole(token: String): String =
        parseClaims(token).get("role", String::class.java)

    fun validateToken(token: String): Boolean = try {
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
