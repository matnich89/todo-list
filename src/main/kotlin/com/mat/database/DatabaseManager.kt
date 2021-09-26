package com.mat.database

import com.mat.entities.ToDo
import com.mat.entities.ToDoDraft
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

class DatabaseManager {

    private val hostname = "localhost"
    private val databaseName = "todo"
    private val username = "root"
    private val password = "password2"

    private val ktormDatabase: Database

    init {
        val jdbcUrl = "jdbc:mysql://$hostname:33061/$databaseName?user=$username&password=$password&useSSL=false"
        ktormDatabase = Database.connect(jdbcUrl)
    }

    fun getAllTodos(): List<DBTodoEntity> {
        return ktormDatabase.sequenceOf(DbTodoTable).toList()
    }

    fun getTodo(id: Int): DBTodoEntity? {
        return ktormDatabase.sequenceOf(DbTodoTable)
            .firstOrNull{it.id eq id}
    }

    fun addTodo(draft: ToDoDraft): ToDo {
        val insertedId = ktormDatabase.insertAndGenerateKey(DbTodoTable) {
            set(DbTodoTable.title, draft.title)
            set(DbTodoTable.done, draft.done)
        }  as Int
        return ToDo(insertedId, draft.title, draft.done)
    }

    fun updateTodo(id: Int, draft: ToDoDraft): Boolean {
        val updatedRows = ktormDatabase.update(DbTodoTable) {
            set(DbTodoTable.title, draft.title)
            set(DbTodoTable.done, draft.done)
            where {
                it.id eq id
            }
        }
        return updatedRows > 0
    }

        fun removeTodo(id: Int): Boolean {
            val deletedRows = ktormDatabase.delete(DbTodoTable) {
                it.id eq id
            }
            return deletedRows > 0
        }

}
