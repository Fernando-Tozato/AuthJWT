package inac.fernando.aulas.projetos.authlogin.authserver.exception

import org.springframework.http.HttpStatus

class NotFoundException(
    message: String
) : ApiException(HttpStatus.NOT_FOUND, message)