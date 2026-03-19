package es.codeurjc.daw.library.controller.rest;

import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.CommentDTO;
import es.codeurjc.daw.library.dto.CommentMapper;
import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.CommentService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserService userService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(HttpServletRequest request, @PathVariable Long id){
        try{
            User user = userService.getUser(request.getUserPrincipal().getName());
            boolean isAdmin = request.isUserInRole("ADMIN");
            Comment deleted = commentService.deleteComment(id, user, isAdmin);
            return ResponseEntity.ok(commentMapper.toDTO(deleted));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public CommentDTO getCommentById(@PathVariable Long id) {
        return commentMapper.toDTO(commentService.getById(id));
    }
    
    
}
