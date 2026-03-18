package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;

import es.codeurjc.daw.library.model.Exercise;

import es.codeurjc.daw.library.model.Exercise;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    public ExerciseDTO toDTO(Exercise exercise);

    public Exercise toEntity(ExerciseDTO exerciseDTO);

    public Exercise toEntity(ExercisePostDTO exercisePostDTO);

    public Exercise toEntity(ExercisePutDTO exercisePutDTO);

    
}
