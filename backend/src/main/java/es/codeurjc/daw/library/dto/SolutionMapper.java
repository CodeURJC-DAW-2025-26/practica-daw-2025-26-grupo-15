package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import es.codeurjc.daw.library.model.Solution;

@Mapper(componentModel = "spring")
public interface SolutionMapper {

    public SolutionDTO toDTO(Solution entity);


    public Solution toEntity(SolutionDTO dto);

    public Solution toEntity(SolutionPostDTO dto);
}
