package es.codeurjc.daw.library.controller.rest;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.PostCreateDTO;
import es.codeurjc.daw.library.dto.PostDTO;
import es.codeurjc.daw.library.dto.PostMapper;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.PostService;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

    @Autowired private PostMapper postMapper;
    @Autowired private PostService postService;
    @Autowired private SearchService searchService;
    @Autowired private UserService userService;


    @GetMapping("/{id}")
    public PostDTO getPostById(@PathVariable Long id) {
        return postMapper.toDTO(postService.getPost(id));
    }
    
    @GetMapping("/")
    public Page<PostDTO> getPosts(Pageable pageable,
                                  @RequestParam(required = false) Long currentUserId){

        Page<Post> postsPage = searchService.searchPosts(pageable, null, currentUserId);
        
        if (postsPage == null) throw new RuntimeException("Unable to find posts page");
        Page<PostDTO> postsDTOPage = postsPage.map(postMapper::toDTO);
        
        return postsDTOPage;
    }

    @PostMapping("/")
    public ResponseEntity<?> postNewPost(Principal principal, @RequestBody PostCreateDTO dto){
        Post post = postMapper.toEntity(dto);
        System.out.println(dto.actionType());
        System.out.println(post.getContentLink());
        System.out.println(post.getDescription());
        System.out.println(post.getHeader());
        User owner = userService.getUser(principal.getName());
        post.setOwner(owner);
        Post saved = postService.createPost(post);
        return ResponseEntity.ok(postMapper.toDTO(saved));    
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostById(HttpServletRequest request, @PathVariable Long id){
        try {
            User requester = userService.getUser(request.getUserPrincipal().getName());
            boolean isAdmin = request.isUserInRole("ADMIN"); 
            Post deleted = postService.deletePost(id, requester, isAdmin);
            return ResponseEntity.ok(postMapper.toDTO(deleted));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    
    
}
