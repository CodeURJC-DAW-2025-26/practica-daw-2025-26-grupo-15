package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.daw.library.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    User toDomain(UserDTO userDTO);
    User toEntity(UserLoginDTO userLoginDTO);
    User fromUserEditDTOtoEntity(UserEditDTO userEditDTO);



}
    

