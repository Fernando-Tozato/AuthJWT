package inac.fernando.aulas.projetos.authlogin.authserver.dto.admin

data class AdminUpdateUserRequest(
    val username: String?,
    val email: String?,
    val roles: List<String>?
)