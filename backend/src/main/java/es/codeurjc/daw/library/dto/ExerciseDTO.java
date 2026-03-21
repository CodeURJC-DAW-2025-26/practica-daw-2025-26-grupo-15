package es.codeurjc.daw.library.dto;

import java.util.List;

public record ExerciseDTO(Long id, String title, String description, int numSolutions, UserBasicInfoDTO owner, ExerciseListBasicInfoDTO exerciseList, List<SolutionBasicInfoDTO> solutions) {
}

