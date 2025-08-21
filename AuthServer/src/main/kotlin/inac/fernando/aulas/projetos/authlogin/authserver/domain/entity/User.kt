package inac.fernando.aulas.projetos.authlogin.authserver.domain.entity

import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminUserResponse
import inac.fernando.aulas.projetos.authlogin.authserver.dto.UserResponse
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID


@Entity
@Table(name = "users", schema = "auth")
class User (
    @Id @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    var enabled: Boolean = true,
    var locked: Boolean = false,

    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", insertable = false, updatable = true)
    var updatedAt: Instant = Instant.now(),

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles", schema = "auth",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()
) {

    fun toResponse(role: Role): UserResponse {
        return UserResponse(
            id = id.toString(),
            username = username,
            email = email,
            role = role.name
        )
    }

    fun toAdminResponse(): AdminUserResponse {
        return AdminUserResponse(
            id = id.toString(),
            username = username,
            email = email,
            enabled = enabled,
            locked = locked,
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString(),
            deletedAt = deletedAt.toString(),
            roles = roles.map { it.name }.sorted()
        )
    }
}

