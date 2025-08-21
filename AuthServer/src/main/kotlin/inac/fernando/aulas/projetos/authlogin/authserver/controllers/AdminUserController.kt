package inac.fernando.aulas.projetos.authlogin.authserver.controllers

import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminCreateUserRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminSetEnabledRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminSetLockedRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminUpdateUserRequest
import inac.fernando.aulas.projetos.authlogin.authserver.dto.admin.AdminUserResponse
import inac.fernando.aulas.projetos.authlogin.authserver.service.AdminUserService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
class AdminUserController(
    private val service: AdminUserService,
) {
    @GetMapping
    fun list(
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) role: String?,
        @RequestParam(required = false) enabled: Boolean?,
        @RequestParam(required = false) locked: Boolean?,
        pageable: Pageable
    ): Page<AdminUserResponse> = service.search(username, email, role, enabled, locked, pageable)

    @PostMapping
    fun create(@RequestBody req: AdminCreateUserRequest): ResponseEntity<AdminUserResponse> =
        ResponseEntity.status(201).body(service.create(req))

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody req: AdminUpdateUserRequest): ResponseEntity<AdminUserResponse> =
        ResponseEntity.ok(service.update(id, req))

    @PatchMapping("/{id}/enabled")
    fun setEnabled(
        @PathVariable id: UUID,
        @RequestBody req: AdminSetEnabledRequest,
        auth: JwtAuthenticationToken
    ): ResponseEntity<AdminUserResponse> =
        ResponseEntity.ok(service.setEnabled(id, req.enabled, currentAdminId(auth)))

    @PatchMapping("/{id}/locked")
    fun setLocked(
        @PathVariable id: UUID,
        @RequestBody req: AdminSetLockedRequest,
        auth: JwtAuthenticationToken
    ): ResponseEntity<AdminUserResponse> =
        ResponseEntity.ok(service.setLocked(id, req.locked, currentAdminId(auth)))

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        auth: JwtAuthenticationToken
    ): ResponseEntity<Void> {
        service.softDelete(id, currentAdminId(auth))
        return ResponseEntity.noContent().build()
    }

    private fun currentAdminId(auth: JwtAuthenticationToken): UUID =
        UUID.fromString(auth.token.claims["uid"].toString())
}