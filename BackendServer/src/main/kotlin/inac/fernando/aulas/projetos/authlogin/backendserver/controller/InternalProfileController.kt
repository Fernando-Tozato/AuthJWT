package inac.fernando.aulas.projetos.authlogin.backendserver.controller

import inac.fernando.aulas.projetos.authlogin.backendserver.dto.InternalCreateProfileRequest
import inac.fernando.aulas.projetos.authlogin.backendserver.dto.ProfileResponse
import inac.fernando.aulas.projetos.authlogin.backendserver.dto.ProfileUpsertRequest
import inac.fernando.aulas.projetos.authlogin.backendserver.service.ProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class InternalProfileController(
    private val profileService: ProfileService
) {

    @PostMapping("/profiles")
    @PreAuthorize("hasRole('SYSTEM') or hasRole('ADMIN')")
    fun createProfile(@RequestBody req: InternalCreateProfileRequest): ResponseEntity<ProfileResponse> {
        val res = profileService.upsertForUser(
            userId = req.userId,
            req = ProfileUpsertRequest(
                firstName = req.firstName,
                lastName = req.lastName
            )
        ).toResponse()

        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }
}