package inac.fernando.aulas.projetos.authlogin.backendserver

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DemoController {

    @GetMapping("/api/public/ping")
    fun publicPing() = mapOf("pong" to true)

    @GetMapping("/api/admin/hello")
    fun adminHello() = mapOf("msg" to "only admins see this")

    @GetMapping("/api/me")
    fun me(@AuthenticationPrincipal jwt: Jwt) = mapOf(
        "sub" to jwt.subject,
        "username" to jwt.getClaim<String>("preferred_username"),
        "email" to jwt.getClaim<String>("email"),
        "roles" to (jwt.getClaim<List<String>>("roles") ?: emptyList())
    )
}