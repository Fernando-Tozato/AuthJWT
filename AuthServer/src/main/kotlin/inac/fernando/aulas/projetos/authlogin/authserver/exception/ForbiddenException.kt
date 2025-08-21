package inac.fernando.aulas.projetos.authlogin.authserver.exception

import org.springframework.http.HttpStatus

class ForbiddenException(
    message: String
) : ApiException(HttpStatus.FORBIDDEN, message)