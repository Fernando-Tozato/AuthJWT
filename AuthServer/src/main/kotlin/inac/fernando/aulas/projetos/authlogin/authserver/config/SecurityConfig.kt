package inac.fernando.aulas.projetos.authlogin.authserver.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.security.oauth2.jwt.JwtDecoder


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    @Qualifier("appCorsConfigurationSource")
    private val corsSource: CorsConfigurationSource,
    private val uds: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) {

    @Bean
    @Order(2)
    fun appSecurityFilterChain(http: HttpSecurity, jwtDecoder: JwtDecoder): SecurityFilterChain = http
        .securityMatcher("/**")
        .cors { it.configurationSource(corsSource) }       // <— CORS AQUI
        .csrf { it.disable() }
        .formLogin { it.disable() }                        // <— evita 302 /login
        .httpBasic { it.disable() }
        .logout { it.disable() }
        .authorizeHttpRequests {
            it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()               // preflight
            it.requestMatchers("/admin/**").hasRole("ADMIN")
            it.requestMatchers(HttpMethod.POST, "/auth/login", "/auth/refresh", "/auth/register").permitAll()
            it.requestMatchers(HttpMethod.GET, "/oauth2/jwks").permitAll()
            it.requestMatchers("/actuator/**", "/error").permitAll()
            it.anyRequest().authenticated()
        }
        .oauth2ResourceServer { rs ->
            rs.jwt { jwt ->
                jwt.decoder(jwtDecoder)
                jwt.jwtAuthenticationConverter(jwtAuthConverter())
            }
        }
        .build()

    private fun jwtAuthConverter(): Converter<Jwt, AbstractAuthenticationToken>  {
        val delegate = JwtAuthenticationConverter()
        delegate.setJwtGrantedAuthoritiesConverter { jwt ->
            val raw = jwt.getClaimAsStringList("roles") ?: emptyList()
            raw.map { r ->
                val name = if (r.startsWith("ROLE_")) r else "ROLE_$r"
                SimpleGrantedAuthority(name)
            }
        }
        return delegate
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}
