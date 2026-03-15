package es.codeurjc.daw.library.dto;

public record PostDTO(
    Long id,
    String header,
    String ownerName,
    String description,
    String timeAgo,
    String contentLink,
    String actionType
){
}
