package inac.fernando.aulas.projetos.authlogin.authserver.domain.entity

import jakarta.persistence.*
import java.time.Instant


@Entity
@Table(name = "roles", schema = "auth")
class Role (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    @Column(nullable = false, unique = true, length = 64)
    var name: String,

    var active: Boolean = true,

    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", insertable = false, updatable = true)
    var updatedAt: Instant = Instant.now()
)