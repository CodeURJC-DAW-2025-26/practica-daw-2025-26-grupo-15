package es.codeurjc.daw.library.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.dto.UserDTO;
import es.codeurjc.daw.library.service.SearchService;

public class ExerciseListRestController {
    
    @Autowired private SearchService searchService;

    @GetMapping("/")
    public Page<ExerciseListDTO> getLists(@RequestParam(required = true ) int page,
                                          @RequestParam(required = true) int size,
                                          @RequestParam Long ownerId,
                                          @RequestParam String nameFilter){
        if (page < 0 || size < 0) throw new IllegalArgumentException("Invalid page or size");

        Page<ExerciseList> listsPage = searchService.searchUsers(page, size, nameFilter, ownerId);
        
        if (listsPage == null) throw new RuntimeException("Unable to find lists page");
        Page<ExerciseListDTO> listsDTOPage = listsPage.map(exerciseListMapper::toDTO);
        
        return listsDTOPage;
    }
}
