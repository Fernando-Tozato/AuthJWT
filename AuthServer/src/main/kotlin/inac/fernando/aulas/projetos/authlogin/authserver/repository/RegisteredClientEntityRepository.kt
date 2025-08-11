package inac.fernando.aulas.projetos.authlogin.authserver.repository

import inac.fernando.aulas.projetos.authlogin.authserver.entity.RegisteredClientEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RegisteredClientEntityRepository
    : JpaRepository<RegisteredClientEntity, String> {

    fun findByClientId(clientId: String): RegisteredClientEntity?
}