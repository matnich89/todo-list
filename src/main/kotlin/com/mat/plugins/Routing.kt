package com.mat.plugins

import com.mat.entities.ToDoDraft
import com.mat.repository.InMemoryToDoRepository
import com.mat.repository.MySQLTodoRepository
import com.mat.repository.ToDoRepository
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {

    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {

        val repository: ToDoRepository = MySQLTodoRepository()

        get("/") {
            call.respondText("Hello World!")
        }

        get("/todos") {
            call.respond(repository.getAllToDos())
        }

        get("/todos/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "id param has to be a number")
                return@get
            }
            val todo = repository.getToDo(id)
            if (todo == null) {
                call.respond(HttpStatusCode.NotFound, "could not find todo with id $id")
            } else {
                call.respond(todo)
            }
            call.respondText("Todo list details for ToDo Item #$id")
        }

        post("/todos") {
            val toDoDraft = call.receive<ToDoDraft>()
            val todo = repository.addTodo(toDoDraft)
            call.respond(todo)
        }

        put("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "id parameter has to be a number!")
                return@put
            }

            val toDoDraft = call.receive<ToDoDraft>()

            val updated = repository.updateTodo(todoId, toDoDraft)

            if (updated) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "no todo found with id $todoId")
            }
        }

        delete("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "id parameter has to be a number!")
                return@delete
            }
        }
    }
}
