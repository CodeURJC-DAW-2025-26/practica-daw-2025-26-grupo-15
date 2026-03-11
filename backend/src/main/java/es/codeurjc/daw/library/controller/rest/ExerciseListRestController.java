package es.codeurjc.daw.library.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.dto.ExerciseListMapper;
import org.springframework.web.bind.annotation.DeleteMapping;

import es.codeurjc.daw.library.dto.ExerciseListDTO;  

@RestController
@RequestMapping("/api/v1/exerciselists")
public class ExerciseListRestController {
    @Autowired
    private ExerciseListMapper exerciseListMapper;

    @Autowired
    private ExerciseListService exerciseListService;

    @GetMapping("/{id}")
    public ExerciseListDTO getExerciseListById(@PathVariable Long id) {
        return exerciseListMapper.toDTO(exerciseListService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ExerciseListDTO deleteExerciseList(@PathVariable Long id) {
        return exerciseListMapper.toDTO(exerciseListService.deleteById(id));
    }
}
