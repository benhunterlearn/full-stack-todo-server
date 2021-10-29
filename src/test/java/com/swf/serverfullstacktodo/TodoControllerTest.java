package com.swf.serverfullstacktodo;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoControllerTest {

    @Autowired
    private MockMvc mvc;

    private TodoRepository repository;
    private TodoItem firstTodoItem;
    private TodoItem secondTodoItem;

    @Autowired
    public TodoControllerTest(TodoRepository repository) {
        this.repository = repository;
    }

    @BeforeEach
    void setUp() {
        this.firstTodoItem = new TodoItem()
                .setContent("first todo")
                .setCompleted(false);
        this.firstTodoItem = repository.save(firstTodoItem);

        this.secondTodoItem = new TodoItem()
                .setContent("second todo")
                .setCompleted(false);
        this.secondTodoItem = repository.save(secondTodoItem);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllTodoItems() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("api/items")
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Is.is(this.firstTodoItem.getId())))
                .andExpect(jsonPath("$[0].content", Is.is(this.firstTodoItem.getContent())))
                .andExpect(jsonPath("$[0].completed", Is.is(this.firstTodoItem.getCompleted())))
                .andExpect(jsonPath("$[1].id", Is.is(this.secondTodoItem.getId())))
                .andExpect(jsonPath("$[1].content", Is.is(this.secondTodoItem.getContent())))
                .andExpect(jsonPath("$[1].completed", Is.is(this.secondTodoItem.getCompleted())));
        // check at least 2 items
    }
}