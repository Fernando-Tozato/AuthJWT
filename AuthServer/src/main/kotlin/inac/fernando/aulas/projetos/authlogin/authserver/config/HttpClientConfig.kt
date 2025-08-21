package inac.fernando.aulas.projetos.authlogin.authserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class HttpClientConfig {
    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()
}