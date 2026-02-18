package com.anirec.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(ex: AuthenticationException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(status = 401, message = ex.message ?: "Unauthorized"))

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(status = 403, message = ex.message ?: "Forbidden"))

    @ExceptionHandler(SyncAlreadyRunningException::class)
    fun handleSyncAlreadyRunning(ex: SyncAlreadyRunningException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(status = 409, message = ex.message ?: "Sync is already running"))

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(status = 500, message = ex.message ?: "Internal Server Error"))
}
