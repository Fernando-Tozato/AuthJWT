package inac.fernando.aulas.projetos.authlogin.authserver.dto.admin

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class AdminCreateUserRequest(
    @field:NotBlank
    val username: String,

    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String,

    val firstName: String?,
    val lastName: String?,
    val roles: List<String> = listOf("ROLE_USER")
)