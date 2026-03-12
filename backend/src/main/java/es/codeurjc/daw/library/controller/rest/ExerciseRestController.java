package es.codeurjc.daw.library.controller.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.service.SearchService;


public class ExerciseRestController {
    
    @Autowired private SearchService searchService;

    @GetMapping("/")
    public Page<ExerciseDTO> getExercises(@RequestParam(required = true ) int page,
                                          @RequestParam(required = true) int size,
                                          @RequestParam Long listId,
                                          @RequestParam String nameFilter){
        if (page < 0 || size < 0) throw new IllegalArgumentException("Invalid page or size");

        Page<Exercise> exercisesPage = searchService.searchExercises(page, size, nameFilter, listId);
        
        if (exercisesPage == null) throw new RuntimeException("Unable to find exercises page");
        Page<ExerciseDTO> exercisesDTOPage= exercisesPage.map(exerciseMapper::toDTO);
        
        return exercisesDTOPage;
    }
}
