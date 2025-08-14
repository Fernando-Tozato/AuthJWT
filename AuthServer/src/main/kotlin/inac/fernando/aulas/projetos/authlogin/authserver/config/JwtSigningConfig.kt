package inac.fernando.aulas.projetos.authlogin.authserver.config

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder

@Configuration
class JwtSigningConfig(
    private val jwkSource: JWKSource<SecurityContext>
) {

    @Bean
    fun jwtEncoder(): JwtEncoder = NimbusJwtEncoder(jwkSource)
}