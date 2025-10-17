package com.smarttodo.smarttodo.Controller;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import com.smarttodo.smarttodo.DTO.TodoDTO;
import com.smarttodo.smarttodo.Entity.Todo;
import com.smarttodo.smarttodo.Exception.MaximumTodoCountException;
import com.smarttodo.smarttodo.Exception.TodosNotFoundException;
import com.smarttodo.smarttodo.Exception.UserNotFoundException;
import com.smarttodo.smarttodo.Mapper.MapStructTodoMapper;
import com.smarttodo.smarttodo.Request.CreateTodoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smarttodo.smarttodo.Response.ApiResponse;
import com.smarttodo.smarttodo.Service.TodoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;


    @PostMapping("/createTodo")
    public ResponseEntity<ApiResponse> createTodo(@Valid @RequestBody CreateTodoRequest req) {
        try {
            Todo todo = todoService.createTodo(req);
            TodoDTO todoDTO = MapStructTodoMapper.INSTANCE.todoToTodoDTO(todo);
            return ResponseEntity.ok(new ApiResponse("Todo created successfully", true, todoDTO));
        } catch (UserNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("A user with this id has not been found", false, e));
        } catch (MaximumTodoCountException e) {
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(new ApiResponse("The user has reached the maximum number of todos allowed", false, null));
        }
    }

    @GetMapping("/getTodos")
    public ResponseEntity<ApiResponse> getTodosByUserId(@RequestParam Long userId) {
        try {
            List<Todo> todos = todoService.getTodosByUserId(userId);
            List<TodoDTO> todoDTOS = MapStructTodoMapper.INSTANCE.todosToTodoDTOs(todos);
            return ResponseEntity.ok(new ApiResponse("Todos retrieved successfully", true, todoDTOS));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("A user with this id has not been found", false, e));
        } catch (TodosNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No todos found for this user", false, e));
        }
    }
}
