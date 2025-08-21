package inac.fernando.aulas.projetos.authlogin.backendserver.controller

import inac.fernando.aulas.projetos.authlogin.backendserver.domain.entity.Profile
import inac.fernando.aulas.projetos.authlogin.backendserver.dto.ProfileResponse
import inac.fernando.aulas.projetos.authlogin.backendserver.dto.ProfileUpsertRequest
import inac.fernando.aulas.projetos.authlogin.backendserver.service.ProfileService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/api/profiles")
class ProfileController(
    private val profileService: ProfileService
) {

    private fun Jwt.userId(): UUID {
        val uid = this.getClaimAsString("uid")
        if (!uid.isNullOrBlank()) return UUID.fromString(uid)

        val sub = this.subject
        if (!sub.isNullOrBlank()) {
            return runCatching { UUID.fromString(sub) }.getOrElse {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token sem claim 'uid' (e 'sub' não é UUID)."
                )
            }
        }
        throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Token sem claim 'uid'."
        )
    }


    // --- Me ---
    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<ProfileResponse> {
        val userId = jwt.userId()
        val res = profileService.getByUserId(userId).toResponse()

        return ResponseEntity.status(HttpStatus.OK).body(res)
    }

    @PutMapping("/me")
    fun putMe(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody req: ProfileUpsertRequest
    ): ResponseEntity<ProfileResponse> {
        val userId = jwt.userId()
        val res = profileService.upsertForUser(userId, req).toResponse()
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }


    // --- Admin ---
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getByUserId(@PathVariable userId: UUID): ResponseEntity<ProfileResponse> {
        val res = profileService.getByUserId(userId).toResponse()
        return ResponseEntity.status(HttpStatus.OK).body(res)
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun createOrUpdate(
        @PathVariable userId: UUID,
        @Valid @RequestBody req: ProfileUpsertRequest
    ): ResponseEntity<ProfileResponse> {
        val res = profileService.adminCreateOrUpdate(userId, req).toResponse()
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteByUserId(@PathVariable userId: UUID): ResponseEntity<Void> {
        profileService.deleteByUserId(userId)
        return ResponseEntity.noContent().build()
    }
}