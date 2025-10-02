package com.example.authentication.repositories

import com.example.authentication.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, String> {

    fun findByEmail(email: String): User?

}