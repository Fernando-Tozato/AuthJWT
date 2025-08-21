package inac.fernando.aulas.projetos.authlogin.authserver.exception

import org.springframework.http.HttpStatus

open class ApiException(
    val status: HttpStatus,
    override val message: String
) : RuntimeException(message)
