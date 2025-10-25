package com.example.authentication.services.impl

import com.example.authentication.repositories.UserRepository
import com.example.authentication.dtos.AuthReqDto
import com.example.authentication.dtos.AuthResDto
import com.example.authentication.models.RefreshToken
import com.example.authentication.models.User
import com.example.authentication.repositories.RefreshTokenRepository
import com.example.authentication.services.AuthService
import com.example.authentication.services.JwtService
import com.example.authentication.utils.HashEncoder
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
): AuthService {
    override fun register(authReqDto: AuthReqDto) {
        var user = userRepository.findByEmail(email = authReqDto.email)
        user?.let {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")
        }

        val hashedPassword = hashEncoder.encode(authReqDto.password)

        if(authReqDto.name.isNullOrBlank()){
            throw ResponseStatusException(HttpStatusCode.valueOf(400), "Username is empty")
        }

        user = User(
            email = authReqDto.email,
            name = authReqDto.name,
            hashedPassword = hashedPassword
        )

        userRepository.save(user)
    }

    override fun loginByEmailAndPassword(authReqDto: AuthReqDto): AuthResDto {

        val user = userRepository.findByEmail(authReqDto.email) ?: throw BadCredentialsException("Invalid Credentials")

        if(!hashEncoder.matches(authReqDto.password, user.hashedPassword)){
            throw BadCredentialsException("Invalid Credentials")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id!!, user.roles.map { role -> role.name })
        val newRefreshToken = jwtService.generateRefreshToken(user.id!!, user.roles.map { role -> role.name })

        storeRefreshToken(user.id!!, newRefreshToken)


        val authResDto = AuthResDto(
            username = user.name,
            email = user.email,
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )

        return authResDto

    }

    @Transactional
    override fun refresh(refreshToken: String): AuthResDto {
        if(!jwtService.validateRefreshToken(refreshToken)){
            throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Refresh Token")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Refresh Token")
        }

        val hashedToken = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id!!, hashedToken)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Refresh token not recognized, maybe used or expired")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id!!, hashedToken)

        val newAccessToken = jwtService.generateAccessToken(user.id!!)
        val newRefreshToken = jwtService.generateRefreshToken(user.id!!)

        storeRefreshToken(user.id, newRefreshToken)
    }

    fun storeRefreshToken(userId: String, refreshToken: String){
        val hashedToken = hashToken(refreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)
        refreshTokenRepository.save(
            RefreshToken(
                hashedToken = hashedToken,
                expiresAt = expiresAt,
                userId = userId
            )
        )
    }

    fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}