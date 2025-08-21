package inac.fernando.aulas.projetos.authlogin.authserver.test.support


import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.request.RequestPostProcessor
import java.util.*

fun adminJwt(uid: UUID = UUID.randomUUID()): RequestPostProcessor =
    jwt()
        .jwt { it.claim("uid", uid.toString()) }
        .authorities(SimpleGrantedAuthority("ROLE_ADMIN"))

fun userJwt(uid: UUID = UUID.randomUUID()): RequestPostProcessor =
    jwt()
        .jwt { it.claim("uid", uid.toString()) }
        .authorities(SimpleGrantedAuthority("ROLE_USER"))