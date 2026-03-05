package es.codeurjc.daw.library.dto;

import java.util.List;

public record UserDTO(
    Long id,
    String name,
    String email,
    String bio,
    String specialty,   
    List<String> roles
) {
}
