package es.codeurjc.daw.library.dto;


public record PostCreateDTO(
    String header,
    String description,
    String contentLink,
    String actionType
){
}
