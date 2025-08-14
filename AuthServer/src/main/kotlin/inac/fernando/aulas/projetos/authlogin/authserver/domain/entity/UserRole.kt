package inac.fernando.aulas.projetos.authlogin.authserver.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_roles", schema = "auth")
class UserRole (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    val role: Role
)