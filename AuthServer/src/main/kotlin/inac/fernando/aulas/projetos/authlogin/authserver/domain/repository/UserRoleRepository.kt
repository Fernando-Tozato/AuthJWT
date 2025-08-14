package inac.fernando.aulas.projetos.authlogin.authserver.domain.repository

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRoleRepository : JpaRepository<UserRole, Long> {
    fun findByUserId(userId: UUID): List<UserRole>
}