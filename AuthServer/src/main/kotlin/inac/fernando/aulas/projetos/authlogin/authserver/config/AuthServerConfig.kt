package inac.fernando.aulas.projetos.authlogin.authserver.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import inac.fernando.aulas.projetos.authlogin.authserver.service.JpaRegisteredClientRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.web.SecurityFilterChain
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.UUID

@Configuration
@EnableWebSecurity
class AuthServerConfig {

    // 0) Chain exclusiva para o JWKS — roda antes de tudo
    @Bean
    @Order(0)
    fun jwkEndpointSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/.well-known/jwks.json")
            .authorizeHttpRequests { auth -> auth.anyRequest().permitAll() }
            .csrf { it.disable() }
        return http.build()
    }

    // 1) Chain do Authorization Server (todos os endpoints OAuth2)
    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer()
        val endpointsMatcher = authorizationServerConfigurer.endpointsMatcher

        http
            .securityMatcher(endpointsMatcher)
            .csrf { it.ignoringRequestMatchers(endpointsMatcher) }
            .apply(authorizationServerConfigurer)
            .and()
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(endpointsMatcher).authenticated()
            }
            .formLogin(withDefaults())

        return http.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        // Qualquer outra requisição exige autenticação
        http
            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
            .formLogin(withDefaults())
        return http.build()
    }

    @Bean
    fun registeredClientRepository(jpaRepo: JpaRegisteredClientRepository): RegisteredClientRepository =
        jpaRepo

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        // Gera um par RSA e expõe como JWKSet
        val keyPair = KeyPairGenerator.getInstance("RSA")
            .apply { initialize(2048) }
            .generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey

        val rsaJwk = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()

        val jwkSet = JWKSet(rsaJwk)
        return ImmutableJWKSet(jwkSet)
    }
}