package inac.fernando.aulas.projetos.authlogin.authserver.controllers

import inac.fernando.aulas.projetos.authlogin.authserver.dto.RegisterRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.UserResponse
import inac.fernando.aulas.projetos.authlogin.authserver.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("auth")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val res = userService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }
}