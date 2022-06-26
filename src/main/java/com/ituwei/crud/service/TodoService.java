package com.ituwei.crud.service;

import com.ituwei.crud.model.TodoItem;
import com.ituwei.crud.repo.TodoItemRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class TodoService {
    private final Logger logger= LoggerFactory.getLogger(TodoService.class);

    @Autowired
    private TodoItemRepo todoItemRepo;

    public TodoService(TodoItemRepo todoItemRepo) {
        this.todoItemRepo = todoItemRepo;
    }


    public Optional<TodoItem> getTodoById(int id) {
        return todoItemRepo.findById(id);
    }


    public void updateTodoService(TodoItem todo) {
        todoItemRepo.updateTodo(todo);
    }

    public Iterable<TodoItem> allTodosService(){
        return todoItemRepo.findAll();
    }

    public void addTodoService(String description, Boolean complete, Instant createdDate, Instant modifiedDate) {
        todoItemRepo.saveTodo(new TodoItem(description, complete, createdDate, modifiedDate));
    }


    public void deleteTodoService(int id) {
        Optional<TodoItem> todo = todoItemRepo.findById(id);
        todo.ifPresent(todoItem -> todoItemRepo.deleteTodo(id));
    }


    public void saveTodoService(TodoItem todo) {
        todoItemRepo.saveTodo(todo);
    }
}
