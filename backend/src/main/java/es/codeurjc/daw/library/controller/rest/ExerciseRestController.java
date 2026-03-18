package es.codeurjc.daw.library.controller.rest;


import java.net.URI;
import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.dto.ExerciseMapper;
import es.codeurjc.daw.library.dto.ExercisePostDTO;
import es.codeurjc.daw.library.dto.ExercisePutDTO;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.dto.ExerciseDTO;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseRestController {
    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired 
    private SearchService searchService;

    @Autowired
    ExerciseService exerciseService;

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public ExerciseDTO getExerciseById(@PathVariable Long id) {
        return exerciseMapper.toDTO(exerciseService.getExercise(id));
    }


    @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteExercise(@PathVariable Long id, HttpServletRequest request) {
            try{
            User user = userService.getUser(request.getUserPrincipal().getName());
            boolean isAdmin = request.isUserInRole("ADMIN");
            Exercise deletedExercise = exerciseService.deleteExercise(id, user, isAdmin);
        
            return ResponseEntity.ok(exerciseMapper.toDTO(deletedExercise));

            } catch (IllegalArgumentException e){
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
            } catch (RuntimeException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
            }

        }

    @PutMapping("/{id}" )
    public ResponseEntity<?> updateExercise(@PathVariable Long id, @RequestBody ExercisePutDTO exercisePutDTO, HttpServletRequest request) {
        try{
        
        User user = userService.getUser(request.getUserPrincipal().getName());

        Exercise updatedExercise = exerciseService.updateExercise(id, exerciseMapper.toEntity(exercisePutDTO), user, null); //TODO: pdf file update
    
        return ResponseEntity.ok(exerciseMapper.toDTO(updatedExercise));
        
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        }catch (RuntimeException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
            }
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
