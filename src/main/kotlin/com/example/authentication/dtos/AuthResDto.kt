package com.example.authentication.dtos

data class AuthResDto (
    val username: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String
)