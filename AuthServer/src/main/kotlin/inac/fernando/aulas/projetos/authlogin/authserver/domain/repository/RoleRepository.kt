package inac.fernando.aulas.projetos.authlogin.authserver.domain.repository

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: String): Optional<Role>

    fun existsByName(name: String): Boolean
}