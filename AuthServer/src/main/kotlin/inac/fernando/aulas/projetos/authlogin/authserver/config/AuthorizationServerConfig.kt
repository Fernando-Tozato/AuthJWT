package inac.fernando.aulas.projetos.authlogin.authserver.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.web.cors.CorsConfigurationSource
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.util.*

@Configuration
class AuthorizationServerConfig(
    @Qualifier("appCorsConfigurationSource")
    private val corsSource: CorsConfigurationSource
) {

    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val authorizationServer = OAuth2AuthorizationServerConfigurer()
        val endpoints = authorizationServer.endpointsMatcher

        http
            .securityMatcher(endpoints)                    // só endpoints do AS
            .cors { it.configurationSource(corsSource) }   // <— CORS AQUI
            .csrf { it.ignoringRequestMatchers(endpoints) }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }
            .with(authorizationServer) { /* OIDC opcional */ }

        return http.build()
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings =
        AuthorizationServerSettings.builder()
            .issuer("http://localhost:9000")
            .build()

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.genKeyPair()
        val rsaKey = RSAKey.Builder(keyPair.public as RSAPublicKey)
            .privateKey(keyPair.private as RSAPrivateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { selector, _ -> selector.select(jwkSet) }
    }

    @Bean
    fun registeredClientRepository(passwordEncoder: PasswordEncoder): RegisteredClientRepository {
        val frontend = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("react-frontend")
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // public client (PKCE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://localhost:5173/callback")
            .postLogoutRedirectUri("http://localhost:5173/")
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(15))
                    .refreshTokenTimeToLive(Duration.ofDays(7))
                    .reuseRefreshTokens(false)
                    .build()
            )
            .clientSettings(
                ClientSettings.builder()
                    .requireProofKey(true) // PKCE
                    .requireAuthorizationConsent(false)
                    .build()
            )
            .build()
        return InMemoryRegisteredClientRepository(frontend)
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }
}