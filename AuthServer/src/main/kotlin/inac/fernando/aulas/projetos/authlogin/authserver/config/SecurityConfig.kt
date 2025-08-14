package inac.fernando.aulas.projetos.authlogin.authserver.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Qualifier("appCorsConfigurationSource")
    private val corsSource: CorsConfigurationSource,
    private val uds: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) {

    @Bean
    @Order(2)
    fun appSecurityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .securityMatcher("/**")
        .cors { it.configurationSource(corsSource) }       // <— CORS AQUI
        .csrf { it.disable() }
        .formLogin { it.disable() }                        // <— evita 302 /login
        .httpBasic { it.disable() }
        .logout { it.disable() }
        .authorizeHttpRequests {
            it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()               // preflight
            it.requestMatchers(HttpMethod.POST, "/auth/login", "/auth/refresh", "/auth/register").permitAll()
            it.requestMatchers(HttpMethod.GET, "/oauth2/jwks").permitAll()
            it.requestMatchers("/actuator/**", "/error").permitAll()
            it.anyRequest().authenticated()
        }
        .exceptionHandling { h ->                          // 401 “limpo” (sem redirect)
            h.authenticationEntryPoint { _, res, _ -> res.sendError(401) }
        }
        .build()

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}
