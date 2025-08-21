package inac.fernando.aulas.projetos.authlogin.authserver.domain.repository

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface UserRepository :
    JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    fun findByEmail(email: String): Optional<User>
    fun findByUsername(username: String): Optional<User>

    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean

    fun existsByUsernameIgnoreCase(username: String): Boolean
    fun existsByEmailIgnoreCase(email: String): Boolean

    @Query(
        """
        select count(u) from User u join u.roles r
        where u.deletedAt is null and u.enabled = true and u.locked = false
          and r.name = 'ROLE_ADMIN' and u.id <> :excluding
        """
    )
    fun countActiveAdmins(excluding: UUID): Long
}