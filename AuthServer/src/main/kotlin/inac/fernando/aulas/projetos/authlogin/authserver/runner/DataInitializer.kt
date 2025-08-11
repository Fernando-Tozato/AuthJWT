package inac.fernando.aulas.projetos.authlogin.authserver.runner

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import java.time.Duration
import java.util.*

@Component
class DataInitializer(
    private val registeredClientRepository: RegisteredClientRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // 1) Cliente para React (Auth Code + PKCE)
        if (registeredClientRepository.findByClientId("react-client") == null) {
            val reactClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("react-client")
                // Para um public client você poderia omitir o secret e usar ClientAuthenticationMethod.NONE
                .clientSecret("{noop}react-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:3000/login/oauth2/code/react-client")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(
                    ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build()
                )
                .tokenSettings(
                    TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .refreshTokenTimeToLive(Duration.ofDays(30))
                        .reuseRefreshTokens(false)
                        .build()
                )
                .build()
            registeredClientRepository.save(reactClient)
        }

        // 2) Cliente para serviço back-to-back (Client Credentials)
        if (registeredClientRepository.findByClientId("backend-service") == null) {
            val backendClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("backend-service")
                .clientSecret("{noop}backend-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("backend.read")
                .clientSettings(ClientSettings.builder().build())
                .tokenSettings(
                    TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(2))
                        .build()
                )
                .build()
            registeredClientRepository.save(backendClient)
        }
    }
}