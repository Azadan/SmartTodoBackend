package com.smarttodo.smarttodo.Mapper;

import com.smarttodo.smarttodo.DTO.UserDTO;
import com.smarttodo.smarttodo.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/*
 * Detta är en MapStructConfig-mappare som konverterar mellan en entitet till en DTO
 * https://mapstruct.org/documentation/reference-guide/
 */

@Mapper
public interface MapStructUserMapper {

    MapStructUserMapper INSTANCE = Mappers.getMapper(MapStructUserMapper.class);


    @Mapping(target = "isAdmin", source = "admin")
    @Mapping(target = "isPremiumUser", source = "premiumUser")
    UserDTO userToUserDTO(User user);
}
