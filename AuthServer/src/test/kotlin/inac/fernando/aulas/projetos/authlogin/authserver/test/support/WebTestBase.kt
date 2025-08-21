package inac.fernando.aulas.projetos.authlogin.authserver.test.support

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
abstract class WebTestBase {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    protected val json = MediaType.APPLICATION_JSON

    @BeforeEach
    fun common() {
        // common setup if needed
    }
}