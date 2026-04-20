package es.codeurjc.daw.library.dto;

import java.sql.Date;

public record SolutionBasicInfoDTO(
    Long id,
    String name,
    UserBasicInfoDTO owner,
    Integer numComments,
    Date lastUpdate
) {
    
} 