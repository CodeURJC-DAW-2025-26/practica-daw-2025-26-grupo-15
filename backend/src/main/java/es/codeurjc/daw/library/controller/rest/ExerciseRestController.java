package es.codeurjc.daw.library.controller.rest;


import java.net.URI;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.dto.ExerciseMapper;
import es.codeurjc.daw.library.dto.ExercisePutDTO;
import es.codeurjc.daw.library.dto.SolutionDTO;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.dto.ExerciseDTO;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.dto.SolutionMapper;
import es.codeurjc.daw.library.dto.SolutionPostDTO;
import es.codeurjc.daw.library.service.SolutionService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseRestController {
    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired 
    private SearchService searchService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private UserService userService;

    @Autowired
    private SolutionMapper solutionMapper;

    @Autowired
    private SolutionService solutionService;

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
        
        }catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }catch (RuntimeException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
            }
    }


    @GetMapping("/")
    public Page<ExerciseDTO> getExercises(Pageable pageable,
                                          @RequestParam(required = false) Long listId,
                                          @RequestParam(required = false) String nameFilter){

        Page<Exercise> exercisesPage = searchService.searchExercises(pageable, nameFilter, listId);
        
        if (exercisesPage == null) throw new RuntimeException("Unable to find exercises page");
        Page<ExerciseDTO> exercisesDTOPage= exercisesPage.map(exerciseMapper::toDTO);
        
        return exercisesDTOPage;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> getExercisePdf(@PathVariable Long id){
        try {
            Exercise exercise = exerciseService.getExercise(id);
            if (exercise.getPdfImage() == null) throw new IllegalArgumentException("No pdf for this exercise");
            Blob blob = exercise.getPdfImage();
            byte[] pdfData = blob.getBytes(1, (int) blob.length());

             return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"exercise-" + exercise.getTitle() + "-" + exercise.getId() + ".pdf\"")
                    .body(pdfData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));    
        } catch(SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage())); 
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));  
        }
    }


    @PutMapping("/{id}/pdf")
    public ResponseEntity<?> uploadExercisePdf(@PathVariable Long id, 
                                                @RequestParam("pdfFile") MultipartFile pdfFile, 
                                                HttpServletRequest request) {
        try {
            User user = userService.getUser(request.getUserPrincipal().getName());

            Exercise updated = exerciseService.uploadPdf(id, user, pdfFile);

            URI location = fromCurrentContextPath().path("/api/v1/exercises/{id}").buildAndExpand(updated.getId()).toUri();

            URI pdfUrl = fromCurrentContextPath().build().toUri();
            return ResponseEntity.ok()
                    .location(location)
                    .body(Map.of(
                            "message", "PDF uploaded successfully",
                            "exerciseId", updated.getId(),
                            "exerciseUrl", location.toString(),
                            "pdfUrl", pdfUrl.toString()
                    ));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/pdf")
    public ResponseEntity<?> deleteExercisePdf(@PathVariable Long id,
                                            HttpServletRequest request) {
        try {
            User user = userService.getUser(request.getUserPrincipal().getName());
            boolean isAdmin = request.isUserInRole("ADMIN");

            Exercise updated = exerciseService.deletePdf(id, user, isAdmin);

            URI location = fromCurrentContextPath()
                    .path("/api/v1/exercises/{id}")
                    .buildAndExpand(updated.getId())
                    .toUri();

            return ResponseEntity.ok()
                    .location(location)
                    .body(Map.of(
                            "message", "PDF deleted successfully",
                            "exerciseId", updated.getId(),
                            "exerciseUrl", location.toString()
                    ));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/{id}/solutions/")
    public ResponseEntity<?> createSolution(@PathVariable Long id, @RequestBody SolutionPostDTO dto, Principal principal) {
        try{
            Solution entity = solutionMapper.toEntity(dto);
            User owner = userService.getUser(principal.getName());
            Solution savedEntity = solutionService.createSolutionWithoutImage(id, entity, owner);
            SolutionDTO createdDTO = solutionMapper.toDTO(savedEntity);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(createdDTO.id()).toUri();
            return ResponseEntity.created(location).body(createdDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        }
    }
    

}
