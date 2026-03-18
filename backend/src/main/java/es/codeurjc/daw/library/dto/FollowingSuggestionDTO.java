package es.codeurjc.daw.library.dto;

import java.util.List;

public record FollowingSuggestionDTO(
    UserBasicInfoDTO suggestion,
    List<UserBasicInfoDTO> contact,
    int commonCount
) {
}
