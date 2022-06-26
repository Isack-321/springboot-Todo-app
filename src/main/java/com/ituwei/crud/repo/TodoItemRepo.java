package com.ituwei.crud.repo;

import com.ituwei.crud.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TodoItemRepo extends CrudRepository<TodoItem,Integer> {

    Optional<TodoItem> findById(int id);

    void updateTodo(TodoItem todo);

    void addTodo(String description, Boolean complete, Instant createdDate, Instant modifiedDate);

    void deleteTodo(int id);

    void saveTodo(TodoItem todo);
}
