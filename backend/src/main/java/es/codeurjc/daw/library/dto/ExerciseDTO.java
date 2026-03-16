package es.codeurjc.daw.library.dto;

public record ExerciseDTO(Long id, String title, String description, int numSolutions, UserIdDTO author) {
}

