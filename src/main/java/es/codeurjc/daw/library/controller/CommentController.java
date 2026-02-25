package es.codeurjc.daw.library.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.service.SolutionService;
import es.codeurjc.daw.library.service.CommentService;
import es.codeurjc.daw.library.service.PostService;

@Controller
public class CommentController {

    @Autowired
    private UserService userService;

    @Autowired
    private SolutionService solutionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @PostMapping("/solution/{id}/comment")
    public String addComment(Model model, @PathVariable Long id, Comment comment, Principal principal){

        User user = resolveUser(principal);
        Solution solution = solutionService.findById(id);
    
        try {
            commentService.createComment(comment, user, solution);
            postService.createPost(new Post(
                user,
                user.getName(),
                "/solution/"+ id,
                "Commented on solution to  "+solution.getExercise().getTitle()  
            ));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "redirect:/solution/" + id;
    }

    private User resolveUser(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            String provider = oauth2Token.getAuthorizedClientRegistrationId();
            String providerId;
            if ("github".equals(provider)) {
                Integer id = oauth2Token.getPrincipal().getAttribute("id");
                providerId = id != null ? id.toString() : null;
            } else {
                providerId = oauth2Token.getPrincipal().getAttribute("sub");
            }
            
            return userService.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> new RuntimeException("OAuth2 user not found in DB"));
        } else {
            return userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }
    
}
