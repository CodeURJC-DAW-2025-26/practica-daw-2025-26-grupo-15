package es.codeurjc.daw.library.dto;

import java.sql.Date;
import java.util.List;


public record ExerciseListDTO(
    Long id,
    String title,
    String description,
    String topic,
    Date lastUpdate,
    UserBasicDTO owner, 
    List<ExerciseBasicInfoDTO> exercises
) {
    
}
