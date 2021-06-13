package com.faskan.todoksb.web

import com.faskan.todoksb.model.Todo
import com.faskan.todoksb.repo.TodoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

const val URL: String = "/api/todos"

@RestController
class TodoResource(val todoRepository: TodoRepository) {
    @GetMapping(URL)
    fun getAllTodos(): List<Todo> {
        return todoRepository.findAll()
    }

    @PostMapping(URL)
    fun createTodo(@RequestBody todo: Todo): Todo {
        return todoRepository.save(todo)
    }
}
