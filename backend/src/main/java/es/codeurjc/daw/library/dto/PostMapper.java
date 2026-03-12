package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import es.codeurjc.daw.library.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
   
    PostDTO toDTO(Post post);
}
