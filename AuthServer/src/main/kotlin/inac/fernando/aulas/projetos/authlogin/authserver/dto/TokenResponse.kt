package inac.fernando.aulas.projetos.authlogin.authserver.dto

data class TokenResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val refreshToken: String? = null
)
