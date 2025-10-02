package com.example.authentication.repositories

import com.example.authentication.models.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository: JpaRepository<RefreshToken, String> {

}