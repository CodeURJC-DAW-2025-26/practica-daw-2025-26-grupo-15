package es.codeurjc.daw.library.controller.rest;


import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.dto.ExerciseMapper;
import es.codeurjc.daw.library.dto.ExercisePostDTO;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.User;
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
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/exercises")
    public class ExerciseRestController {
        @Autowired
        ExerciseMapper exerciseMapper;
    

        @Autowired
        ExerciseService exerciseService;

        @Autowired
        UserService userService;

        @GetMapping("/{id}")
        public ExerciseDTO getExerciseById(@PathVariable Long id) {
            return exerciseMapper.toDTO(exerciseService.getExercise(id));
        }


        @DeleteMapping("/{id}")
        public ExerciseDTO deleteExercise(@PathVariable Long id, HttpServletRequest request) {
            User user = userService.getUser(request.getUserPrincipal().getName());
            boolean isAdmin = request.isUserInRole("ADMIN");
            Exercise deletedExercise = exerciseService.deleteExercise(id, user, isAdmin);
        
            return exerciseMapper.toDTO(deletedExercise);
            
        }
        
    

}
