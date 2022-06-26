package com.ituwei.crud.controller;

import com.ituwei.crud.model.TodoItem;
import com.ituwei.crud.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@RestController
public class TodoItemController {
    private final Logger logger = LoggerFactory.getLogger(TodoItemController.class);

    @Autowired
    private TodoService todoService;

    @GetMapping("/api/todoList")
    public String returnTodoItems(ModelMap model){
    logger.info("get all todos");
    model.put("allTodos",todoService.allTodosService());
    model.put("today", Instant.now().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek());
    return "todoList";
    }
    @PostMapping("/api/create-todo")
    public String createTodoItem(@RequestBody TodoItem item) {
        logger.info("POST request access '/create-todo' path with item: {}", item);
            item.setId(item.getId());
            item.setDescription(item.getDescription());
            item.setComplete(item.getComplete());
            item.setCreatedDate(Instant.now());
            item.setModifiedDate(Instant.now());
            todoService.saveTodoService(item);
            return String.format("Entity created", HttpStatus.CREATED);

    }

    @PutMapping("/api/todoList/{id}")
    public String updateTodoItem(@PathVariable("id") int id,@RequestBody TodoItem item) {
        logger.info("PUT request access '/todolist/update' path with item {}", item);
            Optional<TodoItem> todoItem =  todoService.getTodoById(item.getId());
            if (todoItem.isPresent()) {
                item.setModifiedDate(Instant.now());
                todoService.updateTodoService(item);
                return String.format("Entity updated", HttpStatus.OK);
            }
        return String.format("Not found the entity", HttpStatus.NOT_FOUND);

    }
    @DeleteMapping("/api/delete/{id}")
    public String deleteTodoItem(@PathVariable("id") int id) {
        logger.info("DELETE request access '/api/todolist/{}' path.", id);

            Optional<TodoItem> todoItem = todoService.getTodoById(id);
            if (todoItem.isPresent()) {
                todoService.deleteTodoService(id);
                return String.format("Entity deleted", HttpStatus.OK);
            }
        return String.format("Not found the entity", HttpStatus.NOT_FOUND);

    }
}
