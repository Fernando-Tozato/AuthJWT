package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.client.ProfileClient
import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.User
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.RoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.specification.UserSpecs
import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminCreateUserRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminUpdateUserRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminUserResponse
import inac.fernando.aulas.projetos.authlogin.authserver.exception.ApiException
import inac.fernando.aulas.projetos.authlogin.authserver.exception.BadRequestException
import inac.fernando.aulas.projetos.authlogin.authserver.exception.ConflictException
import inac.fernando.aulas.projetos.authlogin.authserver.exception.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class AdminUserService(
    private val users: UserRepository,
    private val roles: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokens: RefreshTokenService,
    private val profileClient: ProfileClient
) {

    @Transactional(readOnly = true)
    fun search(
        username: String?,
        email: String?,
        role: String?,
        enabled: Boolean?,
        locked: Boolean?,
        pageable: Pageable
    ): Page<AdminUserResponse> {
        var spec: Specification<User> = UserSpecs.notDeleted()
        UserSpecs.usernameContains(username)?.let { spec = spec.and(it) }
        UserSpecs.emailContains(email)?.let { spec = spec.and(it) }
        UserSpecs.hasRole(role)?.let { spec = spec.and(it) }
        UserSpecs.hasEnabled(enabled)?.let { spec = spec.and(it) }
        UserSpecs.hasLocked(locked)?.let { spec = spec.and(it) }
        return users.findAll(spec, pageable).map { it.toAdminResponse() }
    }

    @Transactional
    fun create(req: AdminCreateUserRequest): AdminUserResponse {
        if (users.existsByUsernameIgnoreCase(req.username)) throw ConflictException("username already exists")
        if (users.existsByEmailIgnoreCase(req.email)) throw ConflictException("email already exists")

        val requested = req.roles.toSet()
        val foundRoles = roles.findAllByNameIn(requested)
        if (foundRoles.size != requested.size) throw BadRequestException("unknown roles")

        val u = User(
            username = req.username.trim(),
            email = req.email.trim(),
            passwordHash = passwordEncoder.encode(req.password),
            roles = foundRoles.toMutableSet()
        )

        runCatching {
            profileClient.createProfile(
                userId = u.id,
                first = req.firstName,
                last = req.lastName
            )
        }.onFailure {
            throw ApiException(
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                message = "failed to create profile for new user"
            )
        }

        return users.save(u).toAdminResponse()
    }

    @Transactional
    fun update(id: UUID, req: AdminUpdateUserRequest): AdminUserResponse {
        val u = users.findById(id).orElseThrow { NotFoundException("user not found") }
        if (u.deletedAt != null) throw BadRequestException("user deleted")

        req.username?.let {
            val newU = it.trim()
            if (!newU.equals(u.username, true) && users.existsByUsernameIgnoreCase(newU)) {
                throw ConflictException("username already exists")
            }
            u.username = newU
        }

        req.email?.let {
            val newE = it.trim()
            if (!newE.equals(u.email, true) && users.existsByEmailIgnoreCase(newE)) {
                throw ConflictException("email already exists")
            }
            u.email = newE
        }

        var rolesChanged = false
        req.roles?.let { names ->
            val set = names.toSet()
            val found = roles.findAllByNameIn(set)
            if (found.size != set.size) throw BadRequestException("unknown roles")

            rolesChanged = u.roles.map { it.name }.toSet() != set
            u.roles.clear()
            u.roles.addAll(found)
        }

        u.updatedAt = Instant.now()
        val saved = users.save(u)
        if (rolesChanged) refreshTokens.revokeAllForUser(id)
        return saved.toAdminResponse()
    }

    @Transactional
    fun softDelete(id: UUID, requesterId: UUID) {
        val u = users.findById(id).orElseThrow { NotFoundException("user not found") }
        if (u.deletedAt != null) throw BadRequestException("already deleted")
        if (u.id == requesterId) throw BadRequestException("cannot delete yourself")
        if (isActiveAdmin(u)) ensureAnotherActiveAdminExists(u.id)

        u.enabled = false
        u.locked = true
        u.deletedAt = Instant.now()
        users.save(u)
        refreshTokens.revokeAllForUser(id)
    }

    @Transactional
    fun setEnabled(id: UUID, enabled: Boolean, requesterId: UUID): AdminUserResponse {
        val u = users.findById(id).orElseThrow { NotFoundException("user not found") }
        if (u.deletedAt != null) throw BadRequestException("user deleted")

        if (!enabled && u.id == requesterId) throw BadRequestException("cannot deactivate yourself")
        if (!enabled && isActiveAdmin(u)) ensureAnotherActiveAdminExists(u.id)

        u.enabled = enabled
        u.updatedAt = Instant.now()
        val saved = users.save(u)
        if (!enabled) refreshTokens.revokeAllForUser(id)
        return saved.toAdminResponse()
    }

    @Transactional
    fun setLocked(id: UUID, locked: Boolean, requesterId: UUID): AdminUserResponse {
        val u = users.findById(id).orElseThrow { NotFoundException("user not found") }
        if (u.deletedAt != null) throw BadRequestException("user deleted")

        if (locked && u.id == requesterId) throw BadRequestException("cannot deactivate yourself")
        if (locked && isActiveAdmin(u)) ensureAnotherActiveAdminExists(u.id)

        u.locked = locked
        u.updatedAt = Instant.now()
        val saved = users.save(u)
        if (locked) refreshTokens.revokeAllForUser(id)
        return saved.toAdminResponse()
    }

    private fun isActiveAdmin(u: User): Boolean =
        u.deletedAt == null && u.enabled && !u.locked && u.roles.any { it.name == "ROLE_ADMIN" }

    private fun ensureAnotherActiveAdminExists(excludingId: UUID) {
        val count = users.countActiveAdmins(excludingId)
        if (count <= 0) throw ConflictException("cannot remove last active ADMIN")
    }
}