package com.swf.serverfullstacktodo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Accessors(chain = true)
public class TodoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Boolean completed;

    public void patch(TodoItem todoItem) {
        if (todoItem.getContent() != null) {
            this.content = todoItem.content;
        }
        if (todoItem.getCompleted() != null) {
            this.completed = todoItem.completed;
        }
    }
}
