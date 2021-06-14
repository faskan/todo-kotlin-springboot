package com.faskan.todoksb.web

import com.faskan.todoksb.model.Todo
import com.faskan.todoksb.repo.TodoRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
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
    @LocalServerPort val port: Int = 0,
    @Autowired val todoRepository: TodoRepository
) {

    private fun uri() = "http://localhost:$port/api/todos"

    @BeforeEach
    fun init() {
        todoRepository.deleteAll()
    }

    @Test
    fun `should return all todos`() {
        todoRepository.save(Todo(null, "Find", "Find the letter F"))
        todoRepository.save(Todo(null, "Replace", "Replace id with K"))

        val todosResponse = testRestTemplate.getForEntity(
            uri(), String::class.java
        )
        assertThat(todosResponse.statusCode).isEqualTo(HttpStatus.OK)
        JSONAssert.assertEquals(
            """
                [
                    {
                        "name" : "Find",
                        "description" : "Find the letter F"
                    },
                    {
                        "name" : "Replace",
                        "description" : "Replace id with K"
                    }
                ]
                """, todosResponse.body, JSONCompareMode.LENIENT
        );
    }

    @Test
    fun `should return Todo by id`() {
        val todo = todoRepository.save(Todo(null, "Find", "Find the letter F"));
        todoRepository.save(Todo(null, "Replace", "Replace it by K"));
        var todosResponse = testRestTemplate.getForEntity(
            uri() + "/{id}", String::class.java, todo.id
        );
        assertThat(todosResponse.statusCode).isEqualTo(HttpStatus.OK);
        JSONAssert.assertEquals(
            """
                    {
                        "name" : "Find",
                        "description" : "Find the letter F"
                    }
                """, todosResponse.body, JSONCompareMode.LENIENT
        );
    }

    @Test
    fun `GET Todo should return 404 for an unknown id`() {
        var todosResponse = testRestTemplate.getForEntity(
            uri() + "/{id}", String::class.java, "someUnknownId"
        );
        assertThat(todosResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    fun `should save the todo and return all todos on get`() {
        val responseEntity = testRestTemplate.postForEntity(uri(), request(), Todo::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body?.id?.length).isGreaterThan(0)
        assertThat(responseEntity.body?.id).isNotNull

        val todosResponse = testRestTemplate.getForEntity(
            uri(), String::class.java
        )
        assertThat(todosResponse.statusCode).isEqualTo(HttpStatus.OK)
        JSONAssert.assertEquals(
            """[
                          {
                            "name" : "Deploy",
                            "description" : "Deploy to prod"
                          }
                        ]""", todosResponse.body, JSONCompareMode.LENIENT
        )
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

    @Test
    fun `should update the todo`() {
        val todo = todoRepository.save(Todo(null, "Dont Deploy", "Do not deploy to prod"))
        testRestTemplate.put(uri() + "/{id}", request(), todo.id)
        var todosResponse = testRestTemplate.getForEntity(
            uri() + "/{id}", String::class.java, todo.id
        )
        assertThat(todosResponse.statusCode).isEqualTo(HttpStatus.OK)
        JSONAssert.assertEquals(
            """
                    {
                        "name" : "Deploy",
                        "description" : "Deploy to prod"
                    }
                """, todosResponse.body, JSONCompareMode.LENIENT
        );
    }

    @Test
    fun `should delete todo`() {
        val todo = todoRepository.save(Todo(null, "Dont Deploy", "Do not deploy to prod"))
        testRestTemplate.delete(uri() + "/{id}", todo.id)
        var todosResponse = testRestTemplate.getForEntity(
            uri() + "/", String::class.java, todo.id
        )
        assertThat(todosResponse.statusCode).isEqualTo(HttpStatus.OK)
        JSONAssert.assertEquals(
            """
                [
                ]
                """, todosResponse.body, JSONCompareMode.STRICT
        );
    }
}
