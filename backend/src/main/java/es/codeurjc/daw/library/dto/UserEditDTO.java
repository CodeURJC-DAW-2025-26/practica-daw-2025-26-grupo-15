package es.codeurjc.daw.library.dto;

public record UserEditDTO(
    String name,
    String bio,
    String specialty,
    ImageDTO photo
) {
    
}
