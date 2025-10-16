package com.smarttodo.smarttodo.Mapper;

import com.smarttodo.smarttodo.DTO.TodoDTO;
import com.smarttodo.smarttodo.Entity.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapStructTodoMapper {

    @Mapping(target = "userId", source = "user.userId")
    TodoDTO todoToTodoDTO(Todo todo);
}
