package inac.fernando.aulas.projetos.authlogin.authserver.dto.admin


data class AdminUserResponse(
    val id: String,
    val username: String,
    val email: String,
    val enabled: Boolean,
    val locked: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val roles: List<String>
)
