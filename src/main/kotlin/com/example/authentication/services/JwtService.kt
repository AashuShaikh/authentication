package com.example.authentication.services

import com.example.authentication.models.Role
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.Base64
import java.util.Date
import java.util.UUID

@Component
class JwtService(
    @Value("\${jwt.secret}")
    private val jwtSecret: String
) {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 15L * 60L * 1000L
    val refreshTokenValidityMs = 30L * 24L * 60L * 1000L

    private fun generateToken(
        userId: String,
        roles: List<String>,
        tokenType: String? = null,
        expiryTime: Long
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiryTime)

        return Jwts.builder()
            .subject(userId)
            .claim("roles", roles)
            .claim("type", tokenType)
            .expiration(expiryDate)
            .issuedAt(now)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(
        userId: String,
        roles: List<String>,
    ): String {
        return generateToken(
            userId = userId,
            roles = roles,
            expiryTime = accessTokenValidityMs,
            tokenType = "access"
        )
    }

    fun generateRefreshToken(
        userId: String,
        roles: List<String>,
    ): String {
        return generateToken(
            userId = userId,
            roles = roles,
            expiryTime = refreshTokenValidityMs,
            tokenType = "refresh"
        )
    }

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token = token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "access"
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token = token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "refresh"
    }

    fun getUserIdFromToken(token: String): String {
        val claims = parseAllClaims(token = token) ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Token")
        val userId = claims.subject
        return userId
    }

    fun getRolesFromToken(token: String): List<String> {
        val claims = parseAllClaims(token = token) ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Token")
        val roleNames = claims["roles"] as? List<*> ?: return emptyList()
        return roleNames.mapNotNull { it.toString() }
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = if(token.startsWith("Bearer ")){
            token.removePrefix("Bearer ")
        } else {
            token
        }
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception){
            null
        }
    }

}