package com.smarttodo.smarttodo.Repository;

import com.smarttodo.smarttodo.Entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUser_UserId(Long userId);
}
