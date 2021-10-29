package com.swf.serverfullstacktodo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoControllerTest {

    @Autowired
    private MockMvc mvc;

    private TodoRepository repository;
    private ObjectMapper mapper = new JsonMapper();
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
                .setCompleted(true);
        this.secondTodoItem = repository.save(secondTodoItem);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllTodoItems() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/items")
                .accept(MediaType.APPLICATION_JSON);
        this.mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Is.is(this.firstTodoItem.getId().intValue())))
                .andExpect(jsonPath("$[0].content", Is.is(this.firstTodoItem.getContent())))
                .andExpect(jsonPath("$[0].completed", Is.is(this.firstTodoItem.getCompleted())))
                .andExpect(jsonPath("$[1].id", Is.is(this.secondTodoItem.getId().intValue())))
                .andExpect(jsonPath("$[1].content", Is.is(this.secondTodoItem.getContent())))
                .andExpect(jsonPath("$[1].completed", Is.is(this.secondTodoItem.getCompleted())));
    }

    @Test
    void getTodoItemByIdThatExists() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/items/" + this.firstTodoItem.getId().toString());
        this.mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(this.firstTodoItem.getId().intValue())))
                .andExpect(jsonPath("$.content", Is.is(this.firstTodoItem.getContent())))
                .andExpect(jsonPath("$.completed", Is.is(this.firstTodoItem.getCompleted())));
    }

    @Test
    void postCreatesNewTodoItemInRepositoryAndRespondsWithTodoItemThatHasId() throws Exception {
        TodoItem todoItem = new TodoItem()
                .setContent("this is new")
                .setCompleted(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(todoItem));
        MvcResult result = this.mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.content", Is.is(todoItem.getContent())))
                .andExpect(jsonPath("$.completed", Is.is(todoItem.getCompleted())))
                .andReturn();

        TodoItem responseTodoItem = mapper.readValue(result.getResponse().getContentAsString(), TodoItem.class);
        assertNotNull(responseTodoItem);
    }

    @Test
    void patchUpdatesTodoItemWithNewData() throws Exception {
        TodoItem todoItem = this.repository.findById(this.firstTodoItem.getId()).get();
        String freshMemes = "fresh memes";
        todoItem.setContent(freshMemes);

        String jsonTodoItem = "{\"content\":\"" + freshMemes + "\"" +
                ",\"id\":" + this.firstTodoItem.getId().toString() +
                ",\"completed\":" + this.firstTodoItem.getCompleted().toString() +
                "}";

        // Passing test with:
        // Body = {"id":169,"content":"fresh memes","completed":false}

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/api/items/" + this.firstTodoItem.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(todoItem));
                .content(jsonTodoItem);
        this.mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(this.firstTodoItem.getId().intValue())))
                .andExpect(jsonPath("$.content", Is.is(freshMemes)))
                .andExpect(jsonPath("$.completed", Is.is(this.firstTodoItem.getCompleted())));
    }

    @Test
    void deleteTodoItemByIdThatExists() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/items/" + this.firstTodoItem.getId().toString());
        this.mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));
        assertTrue(this.repository.findById(firstTodoItem.getId()).isEmpty());
    }
}