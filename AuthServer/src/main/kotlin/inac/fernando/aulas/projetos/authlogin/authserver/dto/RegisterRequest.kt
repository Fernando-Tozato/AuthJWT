package inac.fernando.aulas.projetos.authlogin.authserver.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null
)
