package com.smarttodo.smarttodo.DailyReset;

import com.smarttodo.smarttodo.Entity.Todo;
import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Repository.TodoRepository;
import com.smarttodo.smarttodo.Repository.UserRepository;
import com.smarttodo.smarttodo.Service.DailyResetService;
import com.smarttodo.smarttodo.Service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import(DailyResetService.class)
public class DailyRestServiceTest {
    @Autowired
    private DailyResetService resetService;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;
    @Test

    void dailyReset_turnsCompletedTodosToIncomplete() {
        User user = createTestUser();
        Todo completedTodo = createTodo(user, true);
        Todo pendingTodo = createTodo(user, false);


        List<Todo> allTodos = todoRepository.findAll();
        assertThat(allTodos).hasSize(2);

        resetService.dailyReset();
        assertThat(!completedTodo.isCompleted());
        assertThat(!pendingTodo.isCompleted());
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("test" + System.nanoTime() + "@test.com");
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setAdmin(false);
        user.setPremiumUser(false);
        user.setLastDailyReset(null);
        return userRepository.save(user);
    }

    private Todo createTodo(User user, boolean completed) {
        Todo todo = new Todo();
        todo.setTitle("Todo " + System.nanoTime());
        todo.setDescription("Description");
        todo.setCompleted(completed);
        todo.setUser(user);
        return todoRepository.save(todo);
    }
}
