package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import es.codeurjc.daw.library.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    public CommentDTO toDTO(Comment comment);

    public Comment toEntity(CommentDTO dto);

    public Comment toEntity(CommentPostDTO dto);
    
}