package com.smarttodo.smarttodo.Repository;

import com.smarttodo.smarttodo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
