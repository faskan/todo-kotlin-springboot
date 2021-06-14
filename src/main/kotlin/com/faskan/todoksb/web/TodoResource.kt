package com.faskan.todoksb.web

import com.faskan.todoksb.model.Todo
import com.faskan.todoksb.repo.TodoRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

const val URL: String = "/api/todos"

@RestController
class TodoResource(val todoRepository: TodoRepository) {
    @GetMapping(URL)
    fun getAllTodos(): List<Todo> {
        return todoRepository.findAll()
    }
    @GetMapping("$URL/{id}")
    fun getTodo(@PathVariable id: String): Todo {
        return todoRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @PostMapping(URL)
    fun createTodo(@RequestBody todo: Todo): Todo {
        return todoRepository.save(todo)
    }

    @PutMapping("$URL/{id}")
    fun updateTodo(@PathVariable id: String?, @RequestBody todo: Todo): Todo? {
        val updatedTodo = Todo(id, todo.name, todo.description)
        return todoRepository.save(updatedTodo)
    }

    @DeleteMapping("$URL/{id}")
    fun deleteTodo(@PathVariable id: String?) {
        todoRepository.delete(Todo(id, "", ""))
    }
}
