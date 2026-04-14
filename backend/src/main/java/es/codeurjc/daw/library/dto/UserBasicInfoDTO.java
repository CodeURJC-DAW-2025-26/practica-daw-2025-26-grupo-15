package es.codeurjc.daw.library.dto;

public record UserBasicInfoDTO(
    Long id,
    String name,
    String email,
    ImageDTO photo
) {
}