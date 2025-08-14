package inac.fernando.aulas.projetos.authlogin.authserver.domain.repository

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserRoleRepository : JpaRepository<UserRole, Long> {

    fun findByUserId(userId: UUID): List<UserRole>

    @Query("""
    select r.name
    from UserRole ur
    join ur.role r
    where ur.user.id = :userId
""")
    fun findRoleNamesByUserId(userId: UUID): List<String>

}