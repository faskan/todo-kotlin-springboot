package com.faskan.todoksb.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoResource {

    @GetMapping("/api/todos")
    fun getAllTodos(): List<Todo> {
        return listOf(Todo("todo1", "Todo1 Description"),
        Todo("todo2", "Todo2 Description"));
    }
}
