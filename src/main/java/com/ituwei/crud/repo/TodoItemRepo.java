package com.ituwei.crud.repo;

import com.ituwei.crud.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TodoItemRepo extends CrudRepository<TodoItem,Integer> {
}
