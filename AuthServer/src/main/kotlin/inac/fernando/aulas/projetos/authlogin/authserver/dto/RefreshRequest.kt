package inac.fernando.aulas.projetos.authlogin.authserver.dto

import jakarta.validation.constraints.NotBlank

data class RefreshRequest(
    @field:NotBlank val refreshToken: String
)
