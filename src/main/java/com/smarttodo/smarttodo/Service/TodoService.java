package com.smarttodo.smarttodo.Service;

import com.smarttodo.smarttodo.DTO.TodoDTO;
import com.smarttodo.smarttodo.Entity.Todo;
import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Exception.UserNotFoundException;
import com.smarttodo.smarttodo.Mapper.MapStructTodoMapper;
import com.smarttodo.smarttodo.Repository.TodoRepository;
import com.smarttodo.smarttodo.Repository.UserRepository;
import com.smarttodo.smarttodo.Request.CreateTodoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    private final UserRepository userRepository;


    public Todo createTodo(CreateTodoRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new UserNotFoundException("User with this "+ req.getUserId() +" is not found"));
        Todo todo = new Todo();
        todo.setTitle(req.getTitle());
        todo.setDescription(req.getDescription());
        todo.setUser(user);
        return todoRepository.save(todo);
    }
}
