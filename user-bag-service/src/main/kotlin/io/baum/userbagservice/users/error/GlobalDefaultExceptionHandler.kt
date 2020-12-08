package io.baum.userbagservice.users.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalDefaultExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        val code: Int = when(ex) {
            is UnauthorisedUserException -> HttpStatus.UNAUTHORIZED.value()
            is RecordNotFoundException -> HttpStatus.NOT_FOUND.value()
            else -> HttpStatus.INTERNAL_SERVER_ERROR.value()
        }

        return ResponseEntity.status(code).body(ErrorResponse(ex.message))
    }
}