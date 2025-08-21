package inac.fernando.aulas.projetos.authlogin.backendserver.dto

import java.util.UUID

data class ProfileResponse(
    val id: UUID,
    val userId: UUID,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val bio: String?,
    val avatarUrl: String?,
)
