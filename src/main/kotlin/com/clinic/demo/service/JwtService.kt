package com.clinic.demo.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.issuer}") private val issuer: String,
    @Value("\${jwt.audience}") private val audience: String,
    @Value("\${jwt.validity-ms}") private val validityMs: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(userId: Long, role: String): String {
        val now = Date()
        return Jwts.builder()
            .issuer(issuer)
            .audience().add(audience).and()
            .subject(userId.toString())
            .claim("userId", userId)
            .claim("role", role)
            .issuedAt(now)
            .expiration(Date(now.time + validityMs))
            .signWith(key)
            .compact()
    }

    fun parse(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .requireIssuer(issuer)
            .requireAudience(audience)
            .build()
            .parseSignedClaims(token)
            .payload

    fun extractUserId(token: String): Long? = try {
        parse(token).get("userId", Number::class.java)?.toLong()
    } catch (e: Exception) { null }

    fun extractRole(token: String): String? = try {
        parse(token).get("role", String::class.java)
    } catch (e: Exception) { null }
}