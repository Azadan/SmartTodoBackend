package com.smarttodo.smarttodo.Service;

import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Exception.UserAlreadyExistException;
import com.smarttodo.smarttodo.Repository.UserRepository;
import com.smarttodo.smarttodo.Request.CreateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public User createUser(CreateUserRequest req) {
        return Optional.of(req)
                .filter(user -> !userRepository.existsByEmail(user.getEmail()))
                .map(request -> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setPremiumUser(false);
                    user.setAdmin(false);
                    user.setLastDailyReset(null);
                    return userRepository.save(user);
                }).orElseThrow(() -> new UserAlreadyExistException("User with this username already exists"));
    }

    public User createAdminUser(@Valid CreateUserRequest createUserRequest) {
        return Optional.of(createUserRequest)
                .filter(user -> !userRepository.existsByEmail(user.getEmail()))
                .map(request -> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setPremiumUser(true);
                    user.setAdmin(true);
                    user.setLastDailyReset(null);
                    return userRepository.save(user);
                }).orElseThrow(() -> new UserAlreadyExistException("User with this username already exists"));
    }

    public User getLoggedInUser(Authentication authentication) {
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserAlreadyExistException("User with this email not found"));
    }
}
