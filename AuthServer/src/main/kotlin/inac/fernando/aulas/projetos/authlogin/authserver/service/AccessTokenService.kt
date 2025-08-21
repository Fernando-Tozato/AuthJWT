package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.exception.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AccessTokenService(
    private val jwtEncoder: JwtEncoder,
    private val userRepo: UserRepository,
    private val userRoleRepo: UserRoleRepository,
    @Value("\${app.token.access-ttl-minutes:15}")
    private val accessTtlMinutes: Long
) {
    fun issueForUsername(username: String): Pair<String, Instant> {
        val user = userRepo.findByUsername(username).orElseThrow { NotFoundException("user not found") }

        val roles: List<String> = userRoleRepo.findRoleNamesByUserId(user.id)
        val now = Instant.now()
        val expiresAt = now.plusSeconds(accessTtlMinutes * 60)

        val claims = JwtClaimsSet.builder()
            .issuer("http://localhost:9000")
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.username)
            .claim("uid", user.id.toString())
            .claim("roles", roles)
            .build()

        val token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
        return token to expiresAt
    }
}
