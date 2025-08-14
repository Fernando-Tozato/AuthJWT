package inac.fernando.aulas.projetos.authlogin.authserver.config

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

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val uds: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) {
    @Bean
    @Order(2)
    fun appSecurityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { it.disable() }
        .authorizeHttpRequests {
            it.requestMatchers("/actuator/**", "/error").permitAll()
            it.requestMatchers(HttpMethod.POST, "/auth/register").permitAll() // allow registration
            it.anyRequest().authenticated() // this remains the only 'any request' chain (published last)
        }
        .formLogin { } // default login page; customize if needed
        .userDetailsService(uds)
        .build()

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}
