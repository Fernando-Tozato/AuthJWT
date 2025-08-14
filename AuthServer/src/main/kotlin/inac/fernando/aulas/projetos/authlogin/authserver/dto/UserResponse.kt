package inac.fernando.aulas.projetos.authlogin.authserver.dto

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val role: String
)
