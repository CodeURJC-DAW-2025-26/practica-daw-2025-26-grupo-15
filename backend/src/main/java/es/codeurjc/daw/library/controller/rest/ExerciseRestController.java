package es.codeurjc.daw.library.controller.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.dto.ExerciseMapper;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.dto.ExerciseDTO;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.ExerciseDTO;
import es.codeurjc.daw.library.dto.ExerciseMapper;
import es.codeurjc.daw.library.dto.UserDTO;
import es.codeurjc.daw.library.dto.UserMapper;
import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.service.UserService;

@RestController
@RequestMapping("/api/v1/exercises")
    public class ExerciseRestController {
        @Autowired
        ExerciseMapper exerciseMapper;
    

        @Autowired
        ExerciseService exerciseService;

        @GetMapping("/{id}")
        public ExerciseDTO getExerciseById(@PathVariable Long id) {
            return exerciseMapper.toDTO(exerciseService.getExercise(id));
        }
    

}
