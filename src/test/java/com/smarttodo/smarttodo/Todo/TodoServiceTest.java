package com.smarttodo.smarttodo.Todo;

import com.smarttodo.smarttodo.Entity.Todo;
import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Exception.MaximumTodoCountException;
import com.smarttodo.smarttodo.Repository.TodoRepository;
import com.smarttodo.smarttodo.Repository.UserRepository;
import com.smarttodo.smarttodo.Request.CreateTodoRequest;
import com.smarttodo.smarttodo.Service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;



@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import(TodoService.class)
public class TodoServiceTest {

    @Autowired
    TodoService todoService;
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void createTodo() {
        User user = createTestUser(false);

        CreateTodoRequest req = new CreateTodoRequest(user.getUserId(), "Test Todo", "This is a test todo");

        Todo createdTodo = todoService.createTodo(req);

        assertThat(createdTodo.getTodoId()).isNotNull();
        assertThat(createdTodo.getTitle()).isEqualTo("Test Todo");
        assertThat(createdTodo.getDescription()).isEqualTo("This is a test todo");
        assertThat(todoRepository.findAllByUser_UserId(user.getUserId())).hasSize(1);
    }

    @Test
    void createTodo_throwsWhenFreeUserExceedsLimit() {
        User user = createTestUser(false);

        for (int i = 0; i < 5; i++) {
            Todo todo = new Todo();
            todo.setTitle("Title " + i);
            todo.setDescription("Description " + i);
            todo.setUser(user);
            todoRepository.save(todo);
        }

        assertThat(todoRepository.findAllByUser_UserId(user.getUserId())).hasSize(5);
        CreateTodoRequest request = new CreateTodoRequest(user.getUserId(), "Title", "Description text");

        assertThrows(MaximumTodoCountException.class,() -> todoService.createTodo(request));

    }

    @Test
    void createTodo_MoreThanFiveTodoPremiumUser() {
        User user = createTestUser(true);

        for (int i = 0; i < 5; i++) {
            Todo todo = new Todo();
            todo.setTitle("Title " + i);
            todo.setDescription("Description " + i);
            todo.setUser(user);
            todoRepository.save(todo);
        }

        assertThat(todoRepository.findAllByUser_UserId(user.getUserId())).hasSize(5);
        CreateTodoRequest request = new CreateTodoRequest(user.getUserId(), "Title", "Description text");

        assertDoesNotThrow(() -> todoService.createTodo(request));
        assertThat(todoRepository.findAllByUser_UserId(user.getUserId())).hasSize(6);


    }

    @Test
    void toggleTodo_flipsCompletedFlag() {
        User user = createTestUser(false);
        Todo todo = new Todo();
        todo.setTitle("Title");
        todo.setDescription("Description text");
        todo.setUser(user);
        todoRepository.save(todo);

        Todo toggled = todoService.toggleTodo(todo.getTodoId());

        assertThat(toggled.isCompleted()).isTrue();
    }

    private User createTestUser(boolean premium) {
        User user = new User();
        user.setEmail("test" + System.nanoTime() + "@test.com");
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setAdmin(false);
        user.setPremiumUser(premium);
        user.setLastDailyReset(null);
        return userRepository.save(user);
    }
}
