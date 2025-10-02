package com.example.authentication.services

import com.example.authentication.dtos.AuthReqDto
import com.example.authentication.dtos.AuthResDto
import org.springframework.stereotype.Service

interface AuthService {

    fun register(authReqDto: AuthReqDto)

    fun loginByEmailAndPassword(authReqDto: AuthReqDto): AuthResDto

}