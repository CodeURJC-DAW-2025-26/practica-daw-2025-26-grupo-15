package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import es.codeurjc.daw.library.model.ExerciseList;

@Mapper(componentModel = "spring")
public interface ExerciseListMapper {
    
    public ExerciseListDTO toDTO(ExerciseList exerciseList);
}
