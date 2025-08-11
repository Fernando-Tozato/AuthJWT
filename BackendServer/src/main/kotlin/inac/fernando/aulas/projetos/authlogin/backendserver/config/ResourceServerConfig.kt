package inac.fernando.aulas.projetos.authlogin.backendserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class ResourceServerConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Estamos num serviço stateless; CSRF pode ficar desabilitado
            .csrf { it.disable() }
            // Todas as chamadas exigem um token válido
            .authorizeHttpRequests { auth ->
                auth
                    // endpoints públicos (se houver)
                    .requestMatchers("/actuator/health", "/public/**").permitAll()
                    // todo o resto precisa de autenticação
                    .anyRequest().authenticated()
            }
            // configura o suporte a JWT como Resource Server
            .oauth2ResourceServer { rs ->
                rs.jwt(withDefaults())
            }

        return http.build()
    }

    // define a origem do seu React e os métodos/headers permitidos
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cfg = CorsConfiguration()
        cfg.allowedOrigins = listOf("http://localhost:3000")
        cfg.allowedMethods = listOf("GET","POST","PUT","DELETE","OPTIONS")
        cfg.allowedHeaders = listOf("*")
        cfg.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", cfg)
        return source
    }
}