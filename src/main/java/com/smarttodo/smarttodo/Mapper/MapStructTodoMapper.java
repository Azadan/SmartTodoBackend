package com.smarttodo.smarttodo.Mapper;

import com.smarttodo.smarttodo.DTO.TodoDTO;
import com.smarttodo.smarttodo.Entity.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MapStructTodoMapper {

    MapStructTodoMapper INSTANCE = Mappers.getMapper(MapStructTodoMapper.class);

    @Mapping(target = "userId", source = "user.userId")
    TodoDTO todoToTodoDTO(Todo todo);

    List<TodoDTO> todosToTodoDTOs(List<Todo> todos);
}
