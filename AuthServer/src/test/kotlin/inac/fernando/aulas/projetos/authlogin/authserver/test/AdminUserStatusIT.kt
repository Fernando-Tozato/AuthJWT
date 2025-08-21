package inac.fernando.aulas.projetos.authlogin.authserver.test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import inac.fernando.aulas.projetos.authlogin.authserver.test.support.WebTestBase
import inac.fernando.aulas.projetos.authlogin.authserver.test.support.adminJwt
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

class AdminUserStatusIT : WebTestBase() {
    private val om = jacksonObjectMapper()

    private fun createUser(username: String, roles: List<String> = listOf("ROLE_USER")): String {
        val body = mapOf(
            "username" to username,
            "email" to "$username@mail.com",
            "firstName" to username.replaceFirstChar { it.titlecase() },
            "lastName" to "Test",
            "password" to "P4ss!123",
            "roles" to roles
        )
        val res = mockMvc.post("/admin/users") {
            with(adminJwt())
            contentType = json
            content = om.writeValueAsString(body)
        }.andReturn()
        return jacksonObjectMapper().readTree(res.response.contentAsString).get("id").asText()
    }

    private fun lockAllAdminsExcept(exceptId: String?) {
        val res = mockMvc.get("/admin/users") {
            with(adminJwt())
            param("role", "ROLE_ADMIN")
            param("size", "200")
        }.andExpect { status { isOk() } }
            .andReturn().response.contentAsString

        val om = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
        val ids = om.readTree(res).get("content").map { it.get("id").asText() }
        ids.filter { it != exceptId }.forEach { lockById(it) }
    }

    private fun idByUsername(username: String): String? {
        val res = mockMvc.get("/admin/users") {
            with(adminJwt())
            param("username", username)
            param("size", "1")
        }.andExpect { status { isOk() } }
            .andReturn().response.contentAsString

        val root = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().readTree(res)
        val content = root.get("content")
        return if (content != null && content.size() > 0) content[0].get("id").asText() else null
    }

    private fun lockById(id: String) {
        mockMvc.patch("/admin/users/$id/locked") {
            with(adminJwt())
            contentType = json
            content = """{"locked":true}"""
        }.andExpect { status { isOk() } }
    }


    @Test
    fun `desativar e reativar`() {
        val id = createUser("alice")

        mockMvc.patch("/admin/users/$id/enabled") {
            with(adminJwt())
            contentType = json
            content = om.writeValueAsString(mapOf("enabled" to false))
        }.andExpect {
            status { isOk() }
            jsonPath("$.enabled", Matchers.`is`(false))
        }

        mockMvc.patch("/admin/users/$id/enabled") {
            with(adminJwt())
            contentType = json
            content = om.writeValueAsString(mapOf("enabled" to true))
        }.andExpect {
            status { isOk() }
            jsonPath("$.enabled", Matchers.`is`(true))
        }
    }

    @Test
    fun `travar e destravar`() {
        val id = createUser("bob")

        mockMvc.patch("/admin/users/$id/locked") {
            with(adminJwt())
            contentType = json
            content = om.writeValueAsString(mapOf("locked" to true))
        }.andExpect {
            status { isOk() }
            jsonPath("$.locked", Matchers.`is`(true))
        }

        mockMvc.patch("/admin/users/$id/locked") {
            with(adminJwt())
            contentType = json
            content = om.writeValueAsString(mapOf("locked" to false))
        }.andExpect {
            status { isOk() }
            jsonPath("$.locked", Matchers.`is`(false))
        }
    }

    @Test
    fun `nao pode derrubar ultimo admin ativo`() {
        // 1) se existir admin seed, travá-lo para não interferir
        idByUsername("admin")?.let { lockById(it) }

        // 2) cria um único admin ativo (será o "último")
        val lastAdminId = createUser("root2", roles = listOf("ROLE_ADMIN"))
        lockAllAdminsExcept(lastAdminId)

        // 3) tentar travar o ÚNICO admin ativo deve falhar (4xx)
        mockMvc.patch("/admin/users/$lastAdminId/locked") {
            with(adminJwt())
            contentType = json
            content = """{"locked":true}"""
        }.andExpect {
            status { is4xxClientError() }
        }
    }
}