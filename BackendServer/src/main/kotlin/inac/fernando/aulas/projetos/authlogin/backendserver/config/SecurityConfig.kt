package inac.fernando.aulas.projetos.authlogin.backendserver.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableMethodSecurity  // se quiser usar @PreAuthorize
class SecurityConfig(
    @Qualifier("backendCorsSource")
    private val corsSource: CorsConfigurationSource
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // API stateless
            .cors { it.configurationSource(corsSource) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                it.requestMatchers("/api/admin/**").hasRole("ADMIN")  // exige ROLE_ADMIN
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { rs ->
                rs.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthConverter())
                }
            }
        return http.build()
    }

    private fun jwtAuthConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        val rolesConverter = Converter<Jwt, Collection<GrantedAuthority>> { jwt ->
            val roles = (jwt.getClaim<Any>("roles") as? Collection<*>)?.mapNotNull { it?.toString() } ?: emptyList()
            // garante prefixo ROLE_ (se vier sem)
            roles.map { role ->
                val r = if (role.startsWith("ROLE_")) role else "ROLE_${role}"
                SimpleGrantedAuthority(r)
            }
        }

        val scopes = JwtGrantedAuthoritiesConverter() // mantem SCOPE_ para scope/scope
        return Converter { jwt ->
            val authorities = mutableSetOf<GrantedAuthority>()
            authorities.addAll(scopes.convert(jwt) ?: emptyList())
            authorities.addAll(rolesConverter.convert(jwt) ?: emptyList())
            JwtAuthenticationToken(jwt, authorities, jwt.subject)
        }
    }
}
