package es.codeurjc.daw.library.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.daw.library.service.UserService;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface FollowingSuggestionMapper {

    @Mapping(target = "commonCount", expression = "java(userPair.getCommonCount())")
    FollowingSuggestionDTO toDTO(UserService.UserPair userPair);

    List<FollowingSuggestionDTO> toDTOs(List<UserService.UserPair> userPairs);
}