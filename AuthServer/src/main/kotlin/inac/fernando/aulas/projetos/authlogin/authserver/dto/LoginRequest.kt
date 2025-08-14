package inac.fernando.aulas.projetos.authlogin.authserver.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)
