package com.faskan.todoksb.repo

import com.faskan.todoksb.model.Todo
import org.springframework.data.mongodb.repository.MongoRepository

interface TodoRepository : MongoRepository<Todo, String> {
}
