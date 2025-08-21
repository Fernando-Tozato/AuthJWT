package inac.fernando.aulas.projetos.authlogin.authserver.test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import inac.fernando.aulas.projetos.authlogin.authserver.test.support.WebTestBase
import inac.fernando.aulas.projetos.authlogin.authserver.test.support.adminJwt
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.*

class AdminUserSearchIT : WebTestBase() {
    private val om = jacksonObjectMapper()

    @BeforeEach
    fun seed() {
        fun create(username: String, email: String, roles: List<String>) {
            mockMvc.post("/admin/users") {
                with(adminJwt())
                contentType = json
                content = om.writeValueAsString(mapOf(
                    "username" to username,
                    "email" to email,
                    "firstName" to username.replaceFirstChar { it.titlecase() },
                    "lastName" to "Seed",
                    "password" to "P4ss!123",
                    "roles" to roles
                ))
            }.andExpect { status { isCreated() } }
        }
        create("zoe", "zoe@mail.com", listOf("ROLE_USER"))
        create("adam", "adam@corp.com", listOf("ROLE_ADMIN"))
        create("maria", "maria@corp.com", listOf("ROLE_USER", "ROLE_ADMIN"))
    }

    @Test
    fun `filtra por username`() {
        mockMvc.get("/admin/users") {
            with(adminJwt())
            param("username", "a")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[*].username", everyItem(containsStringIgnoringCase("a")))
        }
    }

    @Test
    fun `filtra por email`() {
        mockMvc.get("/admin/users") {
            with(adminJwt())
            param("email", "@corp.com")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()", greaterThanOrEqualTo(2))
        }
    }

    @Test
    fun `filtra por role`() {
        mockMvc.get("/admin/users") {
            with(adminJwt())
            param("role", "ROLE_ADMIN")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[*].roles", everyItem(hasItem("ROLE_ADMIN")))
        }
    }

    @Test
    fun `paginacao`() {
        mockMvc.get("/admin/users") {
            with(adminJwt())
            param("page", "0")
            param("size", "2")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()", lessThanOrEqualTo(2))
            jsonPath("$.totalElements", greaterThanOrEqualTo(3))
        }
    }
}