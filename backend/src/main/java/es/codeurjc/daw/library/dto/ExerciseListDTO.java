package es.codeurjc.daw.library.dto;

import java.sql.Date;

public record ExerciseListDTO(
    Long id,
    String title,
    String description,
    String topic,
    Date lastUpdate
) {
    
}
