package com.example.authentication.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class AuthReqDto (
    @field:Email(
        message = "Invalid Email format"
    )
    val email: String,

    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@#\\\$%^&+=!]).{8,}\\\$",
        message = "Password should be 8 characters long and should include upper case, lower case, a digit and a special character"
    )
    val password: String,

    val name: String? = null,
)