package es.codeurjc.daw.library.controller.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.dto.ExerciseMapper;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.dto.ExerciseDTO;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.service.ExerciseService;

@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseRestController {
    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired 
    private SearchService searchService;

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ExerciseDTO getExerciseById(@PathVariable Long id) {
        return exerciseMapper.toDTO(exerciseService.getExercise(id));
    }

    @GetMapping("/")
    public Page<ExerciseDTO> getExercises(Pageable pageable,
                                          @RequestParam Long listId,
                                          @RequestParam String nameFilter){

        Page<Exercise> exercisesPage = searchService.searchExercises(pageable, nameFilter, listId);
        
        if (exercisesPage == null) throw new RuntimeException("Unable to find exercises page");
        Page<ExerciseDTO> exercisesDTOPage= exercisesPage.map(exerciseMapper::toDTO);
        
        return exercisesDTOPage;
    }
    

}
