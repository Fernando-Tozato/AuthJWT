package inac.fernando.aulas.projetos.authlogin.authserver.test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import inac.fernando.aulas.projetos.authlogin.authserver.test.support.WebTestBase
import inac.fernando.aulas.projetos.authlogin.authserver.test.support.adminJwt
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

class AdminUserCrudIT : WebTestBase() {
    private val om = jacksonObjectMapper()

    @Test
    fun `criar, editar e deletar`() {
        // create
        val body = mapOf(
            "username" to "eva",
            "email" to "eva@mail.com",
            "firstName" to "Eva",
            "lastName" to "Green",
            "password" to "P4ss!123",
            "roles" to listOf("ROLE_USER")
        )

        val id = mockMvc.post("/admin/users") {
            with(adminJwt())
            contentType = json
            content = om.writeValueAsString(body)
        }.andExpect {
            status { isCreated() }
            content { contentTypeCompatibleWith(json) }
            jsonPath("$.id", notNullValue())
            jsonPath("$.enabled", `is`(true))
            jsonPath("$.locked", `is`(false))
            jsonPath("$.roles", contains("ROLE_USER"))
        }.andReturn().response.contentAsString.let { om.readTree(it).get("id").asText() }

        // update username + roles
        mockMvc.put("/admin/users/$id") {
            with(adminJwt())
            contentType = json
            content = om.writeValueAsString(mapOf(
                "username" to "eva2",
                "roles" to listOf("ROLE_USER")
            ))
        }.andExpect {
            status { isOk() }
            jsonPath("$.username", `is`("eva2"))
        }

        // delete
        mockMvc.delete("/admin/users/$id") { with(adminJwt()) }
            .andExpect { status { isNoContent() } }
    }
}