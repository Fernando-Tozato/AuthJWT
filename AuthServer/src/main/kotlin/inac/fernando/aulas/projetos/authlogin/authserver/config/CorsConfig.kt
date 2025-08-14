package inac.fernando.aulas.projetos.authlogin.authserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean("appCorsConfigurationSource")
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cfg = CorsConfiguration()
        cfg.allowedOrigins = listOf("http://localhost:5173")        // origem do front
        cfg.allowedMethods = listOf("GET","POST","PUT","PATCH","DELETE","OPTIONS")
        cfg.allowedHeaders = listOf("Content-Type","Authorization","Accept","Origin","X-Requested-With")
        cfg.exposedHeaders = listOf("Location")
        cfg.allowCredentials = true                                  // ok mesmo sem cookies
        cfg.maxAge = 3600
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", cfg)
        }
    }
}
