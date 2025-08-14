package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.User
import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.UserRole
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.RoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.dto.RegisterRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.UserResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepo: UserRepository,
    private val roleRepo: RoleRepository,
    private val userRoleRepo: UserRoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register (request: RegisterRequest): UserResponse {
        if (userRepo.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        if (userRepo.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val passwordHash = passwordEncoder.encode(request.password)

        val newUser = userRepo.save(
            User(
                username = request.username,
                email = request.email,
                passwordHash = passwordHash
            )
        )

        val userRole = userRoleRepo.save(
            UserRole(
                user = newUser,
                role = roleRepo.findByName("ROLE_USER")
                    .orElseThrow { IllegalStateException("Default role not found") }
            )
        )

        // chamar backend para nome e telefone

        return UserResponse(
            id = newUser.id.toString(),
            username = newUser.username,
            email = newUser.email,
            role = userRole.role.name
        )
    }
}