package inac.fernando.aulas.projetos.authlogin.authserver.test

import inac.fernando.aulas.projetos.authlogin.authserver.test.support.WebTestBase
import inac.fernando.aulas.projetos.authlogin.authserver.test.support.adminJwt
import inac.fernando.aulas.projetos.authlogin.authserver.test.support.userJwt
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class AdminSecurityWebTasks: WebTestBase() {

    @Test
    fun `401 sem token`() {
        mockMvc.get("/admin/users").andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `403 com ROLE_USER`() {
        mockMvc.get("/admin/users") { with(userJwt()) }
            .andExpect { status { isForbidden() } }
    }

    @Test
    fun `200 com ROLE_ADMIN`() {
        mockMvc.get("/admin/users") { with(adminJwt()) }
            .andExpect { status { isOk() } }
    }
}