package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.domain.entity.RefreshToken
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.RefreshTokenRepository
import inac.fernando.aulas.projetos.authlogin.authserver.domain.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.UUID

@Service
class RefreshTokenService(
    private val refreshRepo: RefreshTokenRepository,
    private val userRepo: UserRepository,
    @Value("\${app.token.refresh-ttl-days:7}") private val refreshTtlDays: Long
) {
    fun issueForUsername(username: String): String {
        val user = userRepo.findByUsername(username).orElseThrow()
        val raw = generateRawToken()
        val hash = sha256(raw)
        val expiresAt = Instant.now().plus(refreshTtlDays, ChronoUnit.DAYS)

        refreshRepo.save(
            RefreshToken(
                id = UUID.randomUUID(),
                user = user,
                tokenHash = hash,
                expiresAt = expiresAt,
                revoked = false,
                createdAt = Instant.now()
            )
        )
        return raw // devolvemos o "raw" para o cliente
    }

    @Transactional
    fun rotate(rawProvided: String): Pair<String /*username*/, String /*newRaw*/> {
        val hash = sha256(rawProvided)
        val existing = refreshRepo.findByTokenHashAndRevokedFalse(hash)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token")

        if (existing.expiresAt.isBefore(Instant.now())) {
            existing.revoked = true; refreshRepo.save(existing)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired refresh token")
        }
        existing.revoked = true; refreshRepo.save(existing)

        val user = existing.user
        val newRaw = generateRawToken()
        val newHash = sha256(newRaw)
        val newExpires = Instant.now().plus(refreshTtlDays, ChronoUnit.DAYS)
        refreshRepo.save(
            RefreshToken(UUID.randomUUID(), user, newHash, newExpires, false, Instant.now())
        )
        return user.username to newRaw
    }

    fun revokeAllForUser(userId: UUID) {
        val tokens = refreshRepo.findByUserIdAndRevokedFalse(userId)
        tokens.forEach { it.revoked = true }
        refreshRepo.saveAll(tokens)
    }

    private fun generateRawToken(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun sha256(value: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(value.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(digest)
    }
}
