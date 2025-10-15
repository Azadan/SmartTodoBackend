package com.smarttodo.smarttodo.Mapper;

import com.smarttodo.smarttodo.DTO.UserDTO;
import com.smarttodo.smarttodo.Entity.User;
import org.mapstruct.Mapper;

/*
 * Detta är en MapStruct-mappare som konverterar mellan en entitet till en DTO
 * https://mapstruct.org/documentation/reference-guide/
 */

@Mapper
public interface MapStructUserMapper {

    UserDTO userToUserDTO(User user);
}
