package inac.fernando.aulas.projetos.authlogin.authserver.runner

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.Role
import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.User
import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.UserRole
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.RoleRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRoleRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.DependsOn
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@DependsOn("flyway")
class DataInitializer(
    private val roleRepo: RoleRepository,
    private val userRepo: UserRepository,
    private val userRoleRepo: UserRoleRepository,
    private val encoder: PasswordEncoder,

    @Value("\${app.bootstrap.enabled:true}")
    private val enabled: Boolean,

    @Value("\${app.bootstrap.admin.username:}")
    private val adminUser: String,

    @Value("\${app.bootstrap.admin.email:}")
    private val adminEmail: String,

    @Value("\${app.bootstrap.admin.password:}")
    private val adminPass: String
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(args: ApplicationArguments) {
        if (!enabled) {
            log.info("DataInitializer desabilitado (app.bootstrap.enabled=false).")
            return
        }

        // 1) Garantir roles padrão
        ensureRole("ROLE_USER")
        ensureRole("ROLE_ADMIN")

        // 2) (Opcional) Criar admin de boot se propriedades estiverem preenchidas
        if (adminUser.isNotBlank() && adminEmail.isNotBlank() && adminPass.isNotBlank()) {
            ensureAdminUser(adminUser, adminEmail, adminPass)
        }
    }

    private fun ensureRole(name: String): Role {
        return roleRepo.findByName(name).orElseGet {
            log.info("Criando role: {}", name)
            roleRepo.save(Role(name = name))
        }
    }

    private fun ensureAdminUser(username: String, email: String, rawPass: String) {
        val existing = userRepo.findByUsername(username).orElse(null)
        if (existing != null) {
            log.info("Admin '{}' já existe. Pulando criação.", username)
            return
        }
        log.info("Criando usuário admin '{}'", username)
        val u = userRepo.save(
            User(
                username = username,
                email = email,
                passwordHash = encoder.encode(rawPass),
                enabled = true,
                locked = false
            )
        )
        val adminRole = roleRepo.findByName("ROLE_ADMIN").orElseThrow()
        userRoleRepo.save(UserRole(user = u, role = adminRole))
    }
}