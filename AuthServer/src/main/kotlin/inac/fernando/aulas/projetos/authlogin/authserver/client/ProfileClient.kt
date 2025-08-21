package inac.fernando.aulas.projetos.authlogin.authserver.client

import inac.fernando.aulas.projetos.authlogin.authserver.service.ServiceTokenIssuer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Component
class ProfileClient(
    @Value("\${app.backend.base-url}")
    private val backendBaseUrl: String,

    private val issuer: ServiceTokenIssuer,
    private val builder: WebClient.Builder
) {
    private val webClient by lazy {
        builder.baseUrl(backendBaseUrl).build()
    }

    data class InternalCreateProfileRequest(
        val userId: UUID, val firstName: String?, val lastName: String?
    )

    fun createProfile(userId: UUID, first: String?, last: String?) {
        val token = issuer.issueSystemToken()
        webClient.post()
            .uri("/internal/profiles")
            .headers { it.setBearerAuth(token) }
            .bodyValue(InternalCreateProfileRequest(userId, first, last))
            .retrieve()
            .onStatus({ s -> s.is4xxClientError || s.is5xxServerError }) {
                it.bodyToMono(String::class.java)
                    .map { body -> IllegalStateException("Create profile failed: ${'$'}body") }
            }
            .toBodilessEntity()
            .block() // chamada curta, síncrona
    }
}