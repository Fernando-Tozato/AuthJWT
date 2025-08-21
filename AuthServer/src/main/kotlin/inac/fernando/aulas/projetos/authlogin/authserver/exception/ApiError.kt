package inac.fernando.aulas.projetos.authlogin.authserver.exception

import java.time.Instant

data class ApiError(
    val status: Int,
    val error: String,
    val message: String?,
    val timestamp: Instant = Instant.now()
)
