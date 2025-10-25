package com.example.authentication.controllers

import com.example.authentication.dtos.AuthReqDto
import com.example.authentication.dtos.AuthResDto
import com.example.authentication.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/user-login")
    fun loginByEmailAndPassword(
        @RequestBody authReqDto: AuthReqDto
    ): ResponseEntity<AuthResDto> {
        val authResDto = authService.loginByEmailAndPassword(authReqDto = authReqDto)
        return ResponseEntity.ok(authResDto)
    }

    @PostMapping("/register")
    fun register(
        @RequestBody authReqDto: AuthReqDto
    ) {
        authService.register(authReqDto = authReqDto)
    }

    @PostMapping("/refresh")
    fun refreshToken() : ResponseEntity<AuthResDto> {

    }

}