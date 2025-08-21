package inac.fernando.aulas.projetos.authlogin.authserver.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ServiceTokenIssuer(
    private val jwtEncoder: JwtEncoder,

    private val settings: AuthorizationServerSettings,

    @Value("\${app.service-token.ttl-seconds:60}")
    private val ttl: Long,
) {

    fun issueSystemToken(): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer(settings.issuer)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(ttl))
            .subject("auth-server")
            .claim("roles", listOf("SYSTEM"))
            .claim("aud", "backend")
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}