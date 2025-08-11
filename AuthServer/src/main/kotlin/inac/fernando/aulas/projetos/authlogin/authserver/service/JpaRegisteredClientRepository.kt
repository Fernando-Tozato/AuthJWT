package inac.fernando.aulas.projetos.authlogin.authserver.service

import inac.fernando.aulas.projetos.authlogin.authserver.entity.RegisteredClientEntity
import inac.fernando.aulas.projetos.authlogin.authserver.repository.RegisteredClientEntityRepository
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.stereotype.Service

@Service
@Primary
class JpaRegisteredClientRepository(
    private val repo: RegisteredClientEntityRepository
): RegisteredClientRepository {

    override fun save(registeredClient: RegisteredClient?) {
        registeredClient?.let { rc ->
            val entity = RegisteredClientEntity(
                id = rc.id,
                clientId = rc.clientId,
                clientSecret = rc.clientSecret,
                clientIdIssuedAt = rc.clientIdIssuedAt,
                clientSecretExpiresAt = rc.clientSecretExpiresAt,
                clientName = rc.clientName,
                clientAuthenticationMethods = rc.clientAuthenticationMethods.joinToString(",") { auth -> auth.value },
                authorizationGrantTypes = rc.authorizationGrantTypes.joinToString(",") { grant -> grant.value },
                redirectUris = rc.redirectUris.joinToString(","),
                scopes = rc.scopes.joinToString(","),
                clientSettings = rc.clientSettings.toString(),
                tokenSettings = rc.tokenSettings.toString()
            )
            repo.save(entity)
        }
    }

    override fun findById(id: String): RegisteredClient? =
        repo.findById(id).map(::toRegisteredClient).orElse(null)

    override fun findByClientId(clientId: String): RegisteredClient? =
        repo.findByClientId(clientId)?.let(::toRegisteredClient)

    private fun toRegisteredClient(e: RegisteredClientEntity): RegisteredClient {
        return RegisteredClient.withId(e.id!!)
            .clientId(e.clientId)
            .clientSecret(e.clientSecret)
            .clientName(e.clientName)
            // métodos de autenticação
            .clientAuthenticationMethods {
                e.clientAuthenticationMethods.split(",")
                    .map { ClientAuthenticationMethod(it) }
                    .forEach(it::add)
            }
            // tipos de grant
            .authorizationGrantTypes {
                e.authorizationGrantTypes.split(",")
                    .map { AuthorizationGrantType(it) }
                    .forEach(it::add)
            }
            // URIs e scopes
            .redirectUris     { it.addAll(e.redirectUris.split(",")) }
            .scopes           { it.addAll(e.scopes.split(",")) }
            // settings (aqui só como string bruto; depois podemos parsear JSON)
            .clientSettings(
                ClientSettings.builder()
                    .setting("raw", e.clientSettings)
                    .build()
            )
            .tokenSettings(
                TokenSettings.builder()
                    .setting("raw", e.tokenSettings)
                    .build()
            )
            .build()
    }
}