package es.codeurjc.daw.library.controller.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.dto.ExerciseListMapper;
import es.codeurjc.daw.library.dto.ExerciseMapper;
import es.codeurjc.daw.library.dto.ExercisePostDTO;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.dto.ExerciseListDTO;
import org.springframework.web.bind.annotation.PostMapping;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exerciselists")
public class ExerciseListRestController {
    @Autowired
    private ExerciseListMapper exerciseListMapper;

    @Autowired
    private ExerciseListService exerciseListService;

    @Autowired
    private UserService userService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired
    ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ExerciseListDTO getExerciseListById(@PathVariable Long id) {
        return exerciseListMapper.toDTO(exerciseListService.findById(id));
    }

    @PostMapping("/")
    public ResponseEntity<?> createExerciseList(@RequestBody ExerciseListDTO dto, Principal principal) {
        try{
            ExerciseList exerciseList = exerciseListMapper.toEntity(dto);
            String email = principal.getName();
            User owner = userService.findByEmail(email).orElseThrow();
            ExerciseList savedEntity = exerciseListService.createList(exerciseList, owner);
            ExerciseListDTO createdDTO = exerciseListMapper.toDTO(savedEntity);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(createdDTO.id()).toUri();
            return ResponseEntity.created(location).body(createdDTO);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExerciseList(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        try{
            User user = userService.getUser(principal.getName());
            boolean isAdmin = request.isUserInRole("ADMIN");
            ExerciseList list = exerciseListService.findById(id);
            exerciseListService.deleteList(list, user, isAdmin);
            return ResponseEntity.ok(exerciseListMapper.toDTO(list));
        } catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExerciseList(@PathVariable Long id, @RequestBody ExerciseListDTO exerciseListDTO) {
        try{
            User user = exerciseListService.findById(id).getOwner();
            ExerciseList originalList = exerciseListService.findById(id);
            ExerciseList editedList = exerciseListMapper.toEntity(exerciseListDTO);
            return ResponseEntity.ok(exerciseListMapper.toDTO(exerciseListService.editList(editedList, originalList, user)));
        } catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/exercises/")
        public ResponseEntity<?> postExercise(@RequestBody ExercisePostDTO exercisePostDTO, HttpServletRequest request, @PathVariable Long id) {
            try{
                User user = userService.getUser(request.getUserPrincipal().getName());
                Exercise exercise = exerciseMapper.toEntity(exercisePostDTO);
                Exercise savedExercise = exerciseService.createExercise(exercise, user, null, id);
                URI location = fromCurrentContextPath().path("/api/v1/exercises/{id}").buildAndExpand(savedExercise.getId()).toUri();
                return ResponseEntity.created(location).body(exerciseMapper.toDTO(savedExercise));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
        }

    @GetMapping("/")
    public Page<ExerciseListDTO> getLists(Pageable pageable,
                                          @RequestParam(required = false) Long ownerId,
                                          @RequestParam(required = false) String nameFilter){

        Page<ExerciseList> listsPage = searchService.searchLists(pageable, nameFilter, ownerId);
        
        if (listsPage == null) throw new RuntimeException("Unable to find lists page");
        Page<ExerciseListDTO> listsDTOPage = listsPage.map(exerciseListMapper::toDTO);

        return listsDTOPage;
    }
}
