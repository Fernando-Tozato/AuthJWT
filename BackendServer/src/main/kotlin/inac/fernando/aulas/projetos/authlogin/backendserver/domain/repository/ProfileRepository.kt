package inac.fernando.aulas.projetos.authlogin.backendserver.domain.repository

import inac.fernando.aulas.projetos.authlogin.backendserver.domain.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ProfileRepository : JpaRepository<Profile, UUID> {
    fun findByUserId(userId: UUID): Optional<Profile>
    fun deleteByUserId(userId: UUID): Long
}