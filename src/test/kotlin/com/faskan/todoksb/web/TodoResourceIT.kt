package com.faskan.todoksb.web

import com.faskan.todoksb.model.Todo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.util.CollectionUtils.toMultiValueMap
import org.springframework.util.MultiValueMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoResourceIT(
    @Autowired val testRestTemplate: TestRestTemplate,
    @LocalServerPort val port: Int = 0
) {

    @Test
    fun `should return status ok`() {
        val responseEntity = testRestTemplate.getForEntity(
            uri(), String::class.java
        )
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    private fun uri() = "http://localhost:$port/api/todos"

    @Test
    fun `should save the todo and return all todos on get`() {
        val responseEntity = testRestTemplate.postForEntity(uri(), request(), Todo::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body?.id?.length).isGreaterThan(0)
    }

    private fun request(): HttpEntity<String> {
        return HttpEntity(
            """
            {
                "name" : "Deploy",
                "description" : "Deploy to prod"
            }
            """.trimIndent(), headers()
        )
    }

    private fun headers(): MultiValueMap<String, String>? {
        return toMultiValueMap(mapOf(CONTENT_TYPE to listOf(APPLICATION_JSON.toString())))
    }
}
