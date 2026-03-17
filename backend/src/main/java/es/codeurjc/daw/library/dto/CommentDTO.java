package es.codeurjc.daw.library.dto;

import java.sql.Date;

public record CommentDTO(
    Long id,
    String text,
    Date lastUpdate,
    UserBasicInfoDTO owner
) {
    
}
