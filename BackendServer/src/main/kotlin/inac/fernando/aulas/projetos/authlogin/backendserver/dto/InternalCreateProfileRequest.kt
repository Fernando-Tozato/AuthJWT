package inac.fernando.aulas.projetos.authlogin.backendserver.dto

import java.util.UUID

data class InternalCreateProfileRequest(
    val userId: UUID,
    val firstName: String?,
    val lastName: String?,
)