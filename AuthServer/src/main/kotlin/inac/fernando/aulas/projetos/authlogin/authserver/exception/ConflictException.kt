package inac.fernando.aulas.projetos.authlogin.authserver.exception

import org.springframework.http.HttpStatus

class ConflictException(
    message: String
) : ApiException(HttpStatus.CONFLICT, message)