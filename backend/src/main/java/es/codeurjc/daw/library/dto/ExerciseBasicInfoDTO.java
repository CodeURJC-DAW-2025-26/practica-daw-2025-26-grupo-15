package es.codeurjc.daw.library.dto;

public record ExerciseBasicInfoDTO (
    Long id,
    String title,
    String description,
    int numSolutions
) {
    
}
