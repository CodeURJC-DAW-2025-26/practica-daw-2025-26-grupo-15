package es.codeurjc.daw.library.dto;

public record UserLoginDTO(
    String name,
    String email,
    String encodedPassword
) {
}