package com.smarttodo.smarttodo.Service;

import com.smarttodo.smarttodo.Entity.Todo;
import com.smarttodo.smarttodo.Repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyResetService {

    private final TodoRepository todoRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void dailyReset() {
        List<Todo> completedTodos = todoRepository.findByCompletedTrue();

        for (Todo todo : completedTodos) {
            todo.setCompleted(false);
        }

        todoRepository.saveAll(completedTodos);
    }
}
