package com.smarttodo.smarttodo.Controller;

import com.smarttodo.smarttodo.DTO.UserDTO;
import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Exception.UserAlreadyExistException;
import com.smarttodo.smarttodo.Mapper.MapStructUserMapper;
import com.smarttodo.smarttodo.Request.CreateUserRequest;
import com.smarttodo.smarttodo.Response.ApiResponse;
import com.smarttodo.smarttodo.Service.UserService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    private final MapStructUserMapper userMapper = Mappers.getMapper(MapStructUserMapper.class);


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        try {
            User user = userService.createUser(createUserRequest);
            UserDTO userDTO = userMapper.userToUserDTO(user);
            return ResponseEntity.ok(new ApiResponse("New user has succesfully been created", true, userDTO));
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse("A user with this email already exists", false, e));
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse("Validation error: " + e.getMessage(), false, e));
        }
    }

}
