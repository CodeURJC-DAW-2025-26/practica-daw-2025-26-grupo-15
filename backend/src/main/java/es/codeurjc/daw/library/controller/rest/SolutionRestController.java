package es.codeurjc.daw.library.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;
import java.security.Principal;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import es.codeurjc.daw.library.dto.CommentDTO;
import es.codeurjc.daw.library.dto.CommentMapper;
import es.codeurjc.daw.library.dto.CommentPostDTO;
import es.codeurjc.daw.library.dto.SolutionDTO;
import es.codeurjc.daw.library.dto.SolutionMapper;
import es.codeurjc.daw.library.service.CommentService;
import es.codeurjc.daw.library.service.SolutionPdfExportService;
import es.codeurjc.daw.library.service.SolutionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.codeurjc.daw.library.dto.ImageMapper;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
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
    private ImageMapper imageMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SolutionPdfExportService solutionPdfExportService;

    @GetMapping("/{id}")
    public SolutionDTO getSolutionById(@PathVariable Long id) {
        return solutionMapper.toDTO(solutionService.findById(id));
    }


    @PostMapping("/")
    public ResponseEntity<?> createSolution(@RequestBody SolutionDTO dto, Principal principal) {
        try{
            Solution entity = solutionMapper.toEntity(dto);
            User owner = userService.getUser(principal.getName());
            Solution savedEntity = solutionService.createSolutionWithoutImage(dto.exercise().id(), entity, owner);
            SolutionDTO createdDTO = solutionMapper.toDTO(savedEntity);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(createdDTO.id()).toUri();
            return ResponseEntity.created(location).body(createdDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadSolutionImage(@PathVariable long id, @RequestParam MultipartFile imageFile, Principal principal){
        try{ 
            User user = userService.getUser(principal.getName());
            Solution editedSolution = solutionService.addPhotoToSolution(id, imageFile, user);
            URI location = fromCurrentContextPath().path("/api/v1/images/{imageId}/media").buildAndExpand(editedSolution.getSolImage().getId()).toUri();
            return ResponseEntity.created(location).body(imageMapper.toDTO(editedSolution.getSolImage()));
        }
        catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSolutionById(@PathVariable Long id, HttpServletRequest request, Principal principal) {
        try{
            boolean isAdmin = request.isUserInRole("ADMIN");
            User user = userService.getUser(principal.getName());
            Solution solution = solutionService.findById(id);
            solutionService.deleteSolution(id, user, isAdmin);
            return ResponseEntity.ok(solutionMapper.toDTO(solution));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
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


    @GetMapping("/{id}/pdfs/")
    public  ResponseEntity<?> createSolutionPDF(@PathVariable Long id, Principal principal) {
        try {
            Solution solution = solutionService.getSolutionById(id);
            byte[] pdfData = solutionPdfExportService.generateSolutionPdf(solution);
            String safeName = solutionService.sanitizeFileName(solution.getName());
            String fileName = "solution-" + solution.getId() + "-" + safeName + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(pdfData);
        }
        catch(RuntimeException e) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

}
