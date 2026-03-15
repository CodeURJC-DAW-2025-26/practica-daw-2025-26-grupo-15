package es.codeurjc.daw.library.controller.rest;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;


import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.dto.ExerciseListMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;

import es.codeurjc.daw.library.dto.ExerciseListDTO;
import org.springframework.web.bind.annotation.PostMapping;
import es.codeurjc.daw.library.service.UserService;

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

    @GetMapping("/{id}")
    public ExerciseListDTO getExerciseListById(@PathVariable Long id) {
        return exerciseListMapper.toDTO(exerciseListService.findById(id));
    }

    @PostMapping("/")
    public ExerciseListDTO createExerciseList(@RequestBody ExerciseListDTO dto, Principal principal) {
        ExerciseList entity = exerciseListMapper.toEntity(dto);
        String email = principal.getName();
        User owner = userService.findByEmail(email).orElseThrow();
        entity.setOwner(owner);
        return exerciseListMapper.toDTO(exerciseListService.createList(entity, owner));
    }

    @DeleteMapping("/{id}")
    public ExerciseListDTO deleteExerciseList(@PathVariable Long id) {
        return exerciseListMapper.toDTO(exerciseListService.deleteById(id));
    }

    @PutMapping("/{id}")
    public ExerciseListDTO updateExerciseList(@PathVariable Long id, @RequestBody ExerciseListDTO exerciseListDTO) {
        User user = exerciseListService.findById(id).getOwner();
        return exerciseListMapper.toDTO(exerciseListService.editList(exerciseListMapper.toEntity(exerciseListDTO),
                exerciseListService.findById(id), user));
    }

    @GetMapping("/")
    public Page<ExerciseListDTO> getLists(@RequestParam(required = true ) int page,
                                          @RequestParam(required = true) int size,
                                          @RequestParam Long ownerId,
                                          @RequestParam String nameFilter){
        if (page < 0 || size < 0) throw new IllegalArgumentException("Invalid page or size");

        Page<ExerciseList> listsPage = searchService.searchLists(page, size, nameFilter, ownerId);
        
        if (listsPage == null) throw new RuntimeException("Unable to find lists page");
        Page<ExerciseListDTO> listsDTOPage = listsPage.map(exerciseListMapper::toDTO);
        
        return listsDTOPage;
    }
}
