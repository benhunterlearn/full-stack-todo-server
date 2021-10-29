package com.swf.serverfullstacktodo;

import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<TodoItem, Long> {
}
