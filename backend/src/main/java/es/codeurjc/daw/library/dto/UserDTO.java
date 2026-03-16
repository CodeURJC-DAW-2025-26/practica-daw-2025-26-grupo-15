package es.codeurjc.daw.library.dto;

import java.util.List;

public record UserDTO(
    Long id,
    String name,
    String email,
    String bio,
    String specialty,   
    List<String> roles, 
    List<ExerciseListBasicInfoDTO> exerciseLists,
    List<UserBasicInfoDTO> followers,
    List<UserBasicInfoDTO> following,
    List<UserBasicInfoDTO> requestedFriends,
    List<UserBasicInfoDTO> requestReceived
) {
}
