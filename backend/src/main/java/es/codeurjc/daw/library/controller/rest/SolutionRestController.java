package es.codeurjc.daw.library.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import es.codeurjc.daw.library.dto.CommentDTO;
import es.codeurjc.daw.library.dto.CommentMapper;
import es.codeurjc.daw.library.dto.CommentPostDTO;
import es.codeurjc.daw.library.dto.SolutionDTO;
import es.codeurjc.daw.library.dto.SolutionMapper;
import es.codeurjc.daw.library.service.CommentService;
import es.codeurjc.daw.library.service.SolutionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;


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

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    public SolutionDTO getSolutionById(@PathVariable Long id) {
        return solutionMapper.toDTO(solutionService.findById(id));
    }


    //TODO: implement endpoint to create solution WITH image
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
    public SolutionDTO deleteSolutionById(@PathVariable Long id, Principal principal) {
        
        User user = userService.getUser(principal.getName());
        Solution solution = solutionService.findById(id);
        solutionService.deleteSolution(id, user);
        return solutionMapper.toDTO(solution);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDTO> postComment(@PathVariable Long id, @RequestBody CommentPostDTO dto, Principal principal) {
        Comment comment = commentMapper.toEntity(dto);

        User owner = userService.getUser(principal.getName());
        Solution solution = solutionService.findById(id);
        Comment saved = commentService.createComment(comment, owner, solution);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("api/v1/solutions/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(commentMapper.toDTO(saved));
    }

    @GetMapping("/{id}/comments")
    public List<CommentDTO> getCommentsForSolution(@PathVariable Long id){
        List<Comment> comments = solutionService.findById(id).getComments();  
        List<CommentDTO> dtoList = new LinkedList<>();
        for (Comment c : comments) dtoList.add(commentMapper.toDTO(c)); 
        return dtoList;    
    }

}