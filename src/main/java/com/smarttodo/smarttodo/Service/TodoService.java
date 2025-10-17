package com.smarttodo.smarttodo.Service;

import java.util.List;

import com.smarttodo.smarttodo.Exception.MaximumTodoCountException;
import com.smarttodo.smarttodo.Exception.TodosNotFoundException;
import org.springframework.stereotype.Service;

import com.smarttodo.smarttodo.Entity.Todo;
import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Exception.UserNotFoundException;
import com.smarttodo.smarttodo.Repository.TodoRepository;
import com.smarttodo.smarttodo.Repository.UserRepository;
import com.smarttodo.smarttodo.Request.CreateTodoRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    private final UserRepository userRepository;


    public Todo createTodo(CreateTodoRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new UserNotFoundException("User with this "+ req.getUserId() +" is not found"));
        if (!user.isPremiumUser()){
            long todoCount = todoRepository.countByUser_UserId(req.getUserId());
            if (todoCount >= 5) {
                throw new MaximumTodoCountException("Free users can only create up to 5 todos. Please upgrade to premium to create more.");
            }
        }

        Todo todo = new Todo();
        todo.setTitle(req.getTitle());
        todo.setDescription(req.getDescription());
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    public List<Todo> getTodosByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with this "+ userId +" is not found"));
        if (user==null){
            throw new UserNotFoundException("User with this "+ userId +" is not found");
        }
        List<Todo> todos = todoRepository.findAllByUser_UserId(userId);
        if (todos.isEmpty()) {
            throw new TodosNotFoundException("No todos found for user with ID: " + userId);
        }
        return todos;
    }
}
