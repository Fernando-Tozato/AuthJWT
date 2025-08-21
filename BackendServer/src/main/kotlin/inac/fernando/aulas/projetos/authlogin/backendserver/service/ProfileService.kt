package inac.fernando.aulas.projetos.authlogin.backendserver.service

import inac.fernando.aulas.projetos.authlogin.backendserver.domain.entity.Profile
import inac.fernando.aulas.projetos.authlogin.backendserver.domain.repository.ProfileRepository
import inac.fernando.aulas.projetos.authlogin.backendserver.dto.ProfileUpsertRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class ProfileService(
    private val profileRepo: ProfileRepository
) {

    @Transactional(readOnly = true)
    fun getByUserId(userId: UUID) =
        profileRepo.findByUserId(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")
        }

    @Transactional
    fun upsertForUser(userId: UUID, req: ProfileUpsertRequest): Profile {
        val p = profileRepo.findByUserId(userId).orElse(null)
            ?: Profile(userId = userId)

        p.firstName = req.firstName
        p.lastName = req.lastName
        p.phoneNumber = req.phoneNumber
        p.bio = req.bio
        p.avatarUrl = req.avatarUrl

        return profileRepo.save(p)
    }

    @Transactional
    fun adminCreateOrUpdate(userId: UUID, req: ProfileUpsertRequest): Profile {
        return upsertForUser(userId, req)
    }

    @Transactional
    fun deleteByUserId(userId: UUID) {
        val deleted = profileRepo.deleteByUserId(userId)
        if (deleted == 0L) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found for user ID $userId")
        }
    }
}