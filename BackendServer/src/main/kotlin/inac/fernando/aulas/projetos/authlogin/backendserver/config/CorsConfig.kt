package inac.fernando.aulas.projetos.authlogin.backendserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean("backendCorsSource")
    fun backendCorsSource(): CorsConfigurationSource {
        val cfg =
            CorsConfiguration()
        cfg.allowedOrigins = listOf("http://localhost:5173") // origem do front (Vite)
        cfg.allowedMethods = listOf("GET","POST","PUT","PATCH","DELETE","OPTIONS")
        cfg.allowedHeaders = listOf("Authorization","Content-Type","Accept","Origin","X-Requested-With")
        cfg.exposedHeaders = listOf("Location")
        cfg.allowCredentials = true
        cfg.maxAge = 3600

        val src = UrlBasedCorsConfigurationSource()
        src.registerCorsConfiguration("/**", cfg)
        return src
    }
}