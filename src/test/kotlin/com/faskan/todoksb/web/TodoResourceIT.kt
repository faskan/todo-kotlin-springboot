package com.faskan.todoksb.web

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoResourceIT(@Autowired val testRestTemplate: TestRestTemplate,
                     @LocalServerPort val port: Int = 0) {

    @Test
    fun `should return all todos`() {
        val responseEntity = testRestTemplate.getForEntity("http://localhost:$port/api/todos", String::class.java)
        Assertions.assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        JSONAssert.assertEquals(
            """[
                          {
                            "name" : "todo1",
                            "description" : "Todo1 Description"
                          },
                          {
                            "name" : "todo2",
                            "description" : "Todo2 Description"
                          }
                        ]""", responseEntity.body, JSONCompareMode.STRICT
        )
    }
}
