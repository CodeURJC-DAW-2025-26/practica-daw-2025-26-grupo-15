package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;

import es.codeurjc.daw.library.model.Exercise;

import es.codeurjc.daw.library.model.Exercise;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    public ExerciseDTO toDTO(Exercise exercise);
    
}
