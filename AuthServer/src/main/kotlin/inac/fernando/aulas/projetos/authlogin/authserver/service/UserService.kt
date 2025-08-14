package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.User
import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.UserRole
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.RoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.dto.RegisterRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.UserResponse
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
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun register(dto: RegisterRequest): UserResponse {
        val username = dto.username.trim()
        val email = dto.email.trim().lowercase()

        if (userRepo.existsByUsername(username)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")
        }
        if (userRepo.existsByEmail(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already exists")
        }

        val user = User(
            username = username,
            email = email,
            passwordHash = passwordEncoder.encode(dto.password),
            enabled = true,
            locked = false
        )

        try {
            val newUser = userRepo.save(user)

            val role = roleRepo.findByName("ROLE_USER")
                .orElseThrow { IllegalStateException("ROLE_USER not found (seed not executed)") }

            userRoleRepo.save(UserRole(user = newUser, role = role))

            return newUser.toResponse(role)

        } catch (ex: DataIntegrityViolationException) {
            val msg = ex.message.orEmpty()
            if (msg.contains("username", true)) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")
            }
            if (msg.contains("email", true)) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Email already exists")
            }
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")
        }
    }
}