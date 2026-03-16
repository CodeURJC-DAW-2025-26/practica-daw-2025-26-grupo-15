package es.codeurjc.daw.library.dto;

import java.sql.Date;
import java.util.List;



public record SolutionDTO(
    Long id,
    String name, 
    UserBasicInfoDTO owner,
    Date lastUpdate,
    String description,
    ExerciseBasicInfoDTO exercise, 
    ImageDTO solImage,
    List<CommentDTO> comments
) {

    
} 
  
