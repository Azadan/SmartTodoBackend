package com.smarttodo.smarttodo.Controller;

import com.smarttodo.smarttodo.Exception.UserNotFoundException;
import com.smarttodo.smarttodo.Request.CreateTodoRequest;
import com.smarttodo.smarttodo.Response.ApiResponse;
import com.smarttodo.smarttodo.Service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/createTodo")
    public ResponseEntity<ApiResponse> createTodo(@Valid @RequestBody CreateTodoRequest req) {
        try {
            todoService.createTodo(req);
            return ResponseEntity.ok(new ApiResponse("Todo created successfully", true, null));
        } catch (UserNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("A user with this id has not been found", false, e));
        }
    }
}
