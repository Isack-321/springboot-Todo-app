package com.ituwei.crud.controller;

import com.ituwei.crud.model.TodoItem;
import com.ituwei.crud.repo.TodoItemRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@RestController
public class TodoItemController {
    private final Logger logger = LoggerFactory.getLogger(TodoItemController.class);

    @Autowired
    private TodoItemRepo todoItemRepository;

    public TodoItemController() {
    }

    @RequestMapping("/home")
    public Map<String, Object> home() {
        logger.info("Request '/home' path.");
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("todoItems", todoItemRepository.findAll());
        model.put("today", Instant.now().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek());
        return model;
    }

    @PostMapping("/todolist")
    public ResponseEntity<String> createTodoItem(@RequestBody TodoItem item) {
        logger.info("POST request access '/todolist' path with item: {}", item);
        try {
            item.setId(item.getId());
            item.setDescription(item.getDescription());
            item.setComplete(item.getComplete());
            item.setCreatedDate(Instant.now());
            item.setModifiedDate(Instant.now());
            todoItemRepository.save(item);
            return new ResponseEntity<>("Entity created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Entity creation failed", HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/todolist/{id}")
    public ResponseEntity<String> updateTodoItem(@PathVariable("id") Long id,@RequestBody TodoItem item) {
        logger.info("PUT request access '/api/todolist' path with item {}", item);
        try {
            Optional<TodoItem> todoItem = todoItemRepository.findById(item.getId());
            if (todoItem.isPresent()) {
                item.setModifiedDate(Instant.now());
                todoItemRepository.save(item);
                return new ResponseEntity<>("Entity updated", HttpStatus.OK);
            }
            return new ResponseEntity<>("Not found the entity", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Delete and save errors: ", e);
            return new ResponseEntity<>("Entity updating failed", HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTodoItem(@PathVariable("id") int id) {
        logger.info("DELETE request access '/api/todolist/{}' path.", id);
        try {
            Optional<TodoItem> todoItem = todoItemRepository.findById(id);
            if (todoItem.isPresent()) {
                todoItemRepository.deleteById(id);
                return new ResponseEntity<>("Entity deleted", HttpStatus.OK);
            }
            return new ResponseEntity<>("Not found the entity", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Delete errors: ", e);
            return new ResponseEntity<>("Entity deletion failed", HttpStatus.NOT_FOUND);
        }

    }
}
