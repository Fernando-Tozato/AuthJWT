package inac.fernando.aulas.projetos.authlogin.backendserver.domain.entity

import inac.fernando.aulas.projetos.authlogin.backendserver.dto.ProfileResponse
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "profiles", schema = "app",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id"])])
class Profile(
    @Id
    var id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false, unique = true)
    var userId: UUID,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "bio", columnDefinition = "text")
    var bio: String? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PreUpdate fun touch() { updatedAt = Instant.now() }

    fun toResponse(): ProfileResponse {
        return ProfileResponse(
            id = id,
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            bio = bio,
            avatarUrl = avatarUrl
        )
    }
}