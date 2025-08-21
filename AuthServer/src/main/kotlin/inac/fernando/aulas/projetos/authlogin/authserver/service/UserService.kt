package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.client.ProfileClient
import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.User
import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.UserRole
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.RoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.dto.RegisterRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.UserResponse
import inac.fernando.aulas.projetos.authlogin.authserver.exception.ApiException
import inac.fernando.aulas.projetos.authlogin.authserver.exception.ConflictException
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userRepo: UserRepository,
    private val roleRepo: RoleRepository,
    private val userRoleRepo: UserRoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val profileClient: ProfileClient
) {

    @Transactional
    fun register(req: RegisterRequest): UserResponse {
        if (userRepo.existsByUsernameIgnoreCase(req.username)) throw ConflictException("username already exists")
        if (userRepo.existsByEmailIgnoreCase(req.email)) throw ConflictException("email already exists")

        val user = User(
            username = req.username.trim(),
            email = req.email.trim(),
            passwordHash = passwordEncoder.encode(req.password)
        )

        try {
            val newUser = userRepo.save(user)

            val role = roleRepo.findByName("ROLE_USER")
                .orElseThrow { ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "seed not executed") }

            userRoleRepo.save(UserRole(user = newUser, role = role))

            runCatching {
                profileClient.createProfile(
                    userId = newUser.id,
                    first = req.firstName,
                    last = req.lastName
                )
            }.onFailure {
                throw ApiException(
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                    message = "failed to create profile for new user"
                )
            }

            return newUser.toResponse(role)

        } catch (ex: DataIntegrityViolationException) {
            val msg = ex.message.orEmpty()
            if (msg.contains("username", true)) {
                throw ConflictException("username already exists")
            }
            if (msg.contains("email", true)) {
                throw ConflictException("email already exists")
            }
            throw ConflictException("User already exists")
        }
    }
}