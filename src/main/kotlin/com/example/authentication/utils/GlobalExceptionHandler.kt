package com.example.authentication.utils

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        e: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>>{
        val body = mapOf(
            "status" to e.statusCode,
            "error" to e.reason,
            "timestamp" to Instant.now().toString(),
            "path" to request.requestURI,
            "message" to e.message
        )
        return ResponseEntity(body, e.statusCode)
    }

}