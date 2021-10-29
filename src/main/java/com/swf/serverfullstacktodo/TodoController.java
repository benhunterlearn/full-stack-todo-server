package com.swf.serverfullstacktodo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TodoController {

    private TodoRepository repository;

    @Autowired
    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/items")
    public Iterable<TodoItem> getAllTodoItems() {
        return this.repository.findAll();
    }

    @GetMapping("/items/{id}")
    public TodoItem getTodoItemById(@PathVariable Long id) {
        return this.repository.findById(id).get();
    }

    @PostMapping("/items")
    public TodoItem postCreateTodoItem(@RequestBody TodoItem todoItem) {
        return this.repository.save(todoItem);
    }

    @PatchMapping("/items/{id}")
    public TodoItem patchTodoItem(@PathVariable Long id, @RequestBody TodoItem todoItem) {

        System.out.println("PATCH request for id: " + id);
        System.out.println("    Content: " + todoItem.toString());

        TodoItem currentTodoItem = this.repository.findById(id).get();
        currentTodoItem.patch(todoItem);

        System.out.println("    Saving TodoItem: " + currentTodoItem.toString());
        return this.repository.save(currentTodoItem);
    }

    @DeleteMapping("/items/{id}")
    public String deleteTodoItemById(@PathVariable Long id) {
        this.repository.deleteById(id);
        return "SUCCESS";
    }
}
