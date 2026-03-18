package es.codeurjc.daw.library.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.daw.library.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
    UserBasicInfoDTO toBasicInfoDTO(User user);
    List<UserBasicInfoDTO> toBasicInfoDTOs(List<User> users);

    User toDomain(UserDTO userDTO);
    User toEntity(UserLoginDTO userLoginDTO);
    User fromUserEditDTOtoEntity(UserEditDTO userEditDTO);



}
    

