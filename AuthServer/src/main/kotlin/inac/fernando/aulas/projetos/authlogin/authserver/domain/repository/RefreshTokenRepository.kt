package inac.fernando.aulas.projetos.authlogin.authserver.domain.repository

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    fun findByUserIdAndRevokedFalse(userId: UUID): List<RefreshToken>
}