package inac.fernando.aulas.projetos.authlogin.backendserver

import inac.fernando.aulas.projetos.authlogin.backendserver.config.SecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt

@WebMvcTest(controllers = [DemoController::class])
@Import(SecurityConfig::class)
class SecurityWebTests(@Autowired val mvc: MockMvc) {

    @Test
    fun `publico - 200 sem token`() {
        mvc.get("/api/public/ping").andExpect { status { isOk() } }
    }

    @Test
    fun `me - 401 sem token`() {
        mvc.get("/api/me").andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `me - 200 com token`() {
        mvc.get("/api/me") {
            with(jwt().jwt { j ->
                j.claim("preferred_username", "jdoe")
                j.claim("email", "j@x.com")
                j.claim("roles", listOf("ROLE_USER"))
                j.subject("123")
            })
        }.andExpect { status { isOk() } }
    }

    @Test
    fun `admin - 403 somente USER`() {
        mvc.get("/api/admin/hello") {
            with(jwt().jwt { it.claim("roles", listOf("ROLE_USER")) })
        }.andExpect { status { isForbidden() } }
    }

    @Test
    fun `admin - 200 com ROLE_ADMIN`() {
        mvc.get("/api/admin/hello") {
            with(
                jwt().jwt { j ->
                    j.claim("preferred_username", "admin")
                    j.claim("roles", listOf("ROLE_ADMIN")) // opcional – só para ficar realista
                    j.subject("123")
                }.authorities(SimpleGrantedAuthority("ROLE_ADMIN")) // <- ESSENCIAL
            )
        }.andExpect { status { isOk() } }
    }
}
