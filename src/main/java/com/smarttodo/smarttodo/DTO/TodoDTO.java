package com.smarttodo.smarttodo.DTO;


public record TodoDTO(Long todoId, Long userId, String title, String description, boolean completed) {
}
