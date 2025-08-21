package inac.fernando.aulas.projetos.authlogin.authserver.exception

import org.springframework.http.HttpStatus

class BadRequestException(
    message: String
) : ApiException(HttpStatus.BAD_REQUEST, message)