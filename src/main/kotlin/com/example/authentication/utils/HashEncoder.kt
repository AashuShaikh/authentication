package com.example.authentication.utils

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {

    private val bCrypt = BCryptPasswordEncoder()

    fun encode(raw: String): String = bCrypt.encode(raw)

    fun matches(raw: String, hashed: String): Boolean = bCrypt.matches(raw, hashed)

}