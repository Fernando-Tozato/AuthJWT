package inac.fernando.aulas.projetos.authlogin.authserver.domain.repository

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {

    fun findByEmail(email: String): Optional<User>
    fun findByUsername(username: String): Optional<User>

    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
}