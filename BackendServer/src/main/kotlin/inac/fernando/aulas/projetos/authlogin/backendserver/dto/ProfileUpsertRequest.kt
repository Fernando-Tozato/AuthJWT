package inac.fernando.aulas.projetos.authlogin.backendserver.dto

import jakarta.validation.constraints.Size

data class ProfileUpsertRequest(
    @field:Size(max = 100)
    val firstName: String? = null,

    @field:Size(max = 100)
    val lastName: String? = null,

    val phoneNumber: String? = null,

    val bio: String? = null,

    val avatarUrl: String? = null
)
