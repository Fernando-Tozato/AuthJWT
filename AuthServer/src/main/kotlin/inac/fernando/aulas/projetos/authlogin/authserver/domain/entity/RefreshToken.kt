package inac.fernando.aulas.projetos.authlogin.authserver.domain.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "refresh_tokens", schema = "auth")
class RefreshToken (
    @Id @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "token_hash", nullable = false)
    var tokenHash: String,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,

    var revoked: Boolean = false,

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now(),
)