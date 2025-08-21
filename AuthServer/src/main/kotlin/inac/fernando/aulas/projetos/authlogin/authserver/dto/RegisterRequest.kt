package inac.fernando.aulas.projetos.authlogin.authserver.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank
    val username: String,

    @field:Email
    val email: String,

    @field:Size(min = 8)
    val password: String,

    val firstName: String? = null,
    val lastName: String? = null
)
