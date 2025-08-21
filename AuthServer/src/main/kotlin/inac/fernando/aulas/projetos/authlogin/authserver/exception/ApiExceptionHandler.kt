package inac.fernando.aulas.projetos.authlogin.authserver.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    private fun body(status: HttpStatus, message: String?): ApiError {
        return ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = message
        )
    }

    @ExceptionHandler(ApiException::class)
    fun handleApi(e: ApiException): ResponseEntity<ApiError> {
        return ResponseEntity.status(e.status).body(
            body(e.status, e.message)
        )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(e: BadCredentialsException): ResponseEntity<ApiError> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            body(HttpStatus.UNAUTHORIZED, e.message ?: "Invalid credentials")
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class, ConstraintViolationException::class)
    fun handleValidation(e: Exception): ResponseEntity<ApiError> {
        val message = when (e) {
            is MethodArgumentNotValidException -> e.bindingResult.fieldErrors.joinToString(", ") { it.defaultMessage ?: "Invalid field" }
            is ConstraintViolationException -> e.constraintViolations.joinToString(", ") { it.message ?: "Invalid constraint" }
            else -> "Validation error"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            body(HttpStatus.BAD_REQUEST, message)
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(e: NoSuchElementException): ResponseEntity<ApiError> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            body(HttpStatus.NOT_FOUND, e.message ?: "Resource not found")
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleOther(e: Exception): ResponseEntity<ApiError> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            body(HttpStatus.INTERNAL_SERVER_ERROR, e.message ?: "An unexpected error occurred")
        )
    }
}