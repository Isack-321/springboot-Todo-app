package com.ituwei.crud.service;

import com.ituwei.crud.model.TodoItem;
import com.ituwei.crud.repo.TodoItemRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class TodoService implements CommandLineRunner {
    private final Logger logger= LoggerFactory.getLogger(TodoService.class);

    @Autowired
    private TodoItemRepo todoItemRepo;
    @Override
    public void run(String... args) throws Exception {

    }
    private void loadSeedData() {
        if (todoItemRepo.count() == 0) {
            TodoItem todoItem1 = new TodoItem();
            TodoItem todoItem2 = new TodoItem();

            todoItemRepo.save(todoItem1);
            todoItemRepo.save(todoItem2);
        }

        logger.info("Number of TodoItems: {}", todoItemRepo.count());
    }
}
