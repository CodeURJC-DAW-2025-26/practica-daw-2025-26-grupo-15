package es.codeurjc.daw.library.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.codeurjc.daw.library.dto.ExerciseListDTO;
import es.codeurjc.daw.library.dto.SolutionDTO;
import es.codeurjc.daw.library.dto.SolutionMapper;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.service.SolutionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/solutions")
public class SolutionRestController {

    @Autowired
    private SolutionMapper solutionMapper;

    @Autowired
    private SolutionService solutionService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public SolutionDTO getSolutionById(@PathVariable Long id) {
        return solutionMapper.toDTO(solutionService.findById(id));
    }

    @PostMapping("/")
    public ResponseEntity<SolutionDTO> postSolutionById(@RequestBody SolutionDTO dto, Principal principal) {
        Solution entity = solutionMapper.toEntity(dto);

        User owner = userService.getUser(principal.getName());
        entity.setOwner(owner);
        Solution savedEntity = solutionService.createSolutionWithoutImage(dto.exercise().id(), entity, owner);

        SolutionDTO createdDTO = solutionMapper.toDTO(savedEntity);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(createdDTO.id()).toUri();

        return ResponseEntity.created(location).body(createdDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolutionById(@PathVariable Long id, HttpServletRequest request,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isAdmin = request.isUserInRole("ADMIN");
        User user = userService.getUser(principal.getName());
        solutionService.deleteSolution(id, user, isAdmin);
        return ResponseEntity.noContent().build();
    }

}
