package com.smarttodo.smarttodo.Response;

import com.smarttodo.smarttodo.DTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthResponse {
    private String jwt;
    private UserDTO userDTO;}
