package inac.fernando.aulas.projetos.authlogin.backendserver.controller


import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/api/hello")
    @PreAuthorize("hasAuthority('SCOPE_backend.read')")
    fun hello(authentication: JwtAuthenticationToken): String {
        return "Olá, ${authentication.token.subject}! Seu token é válido."
    }
}