package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.PostService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class WebController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    
     @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        boolean isAuthenticated = principal != null && !(principal instanceof AnonymousAuthenticationToken);
        if (isAuthenticated) {
            model.addAttribute("logged", true);
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }
    }
    

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        if (principal != null) {
            User user = resolveUser(principal);
            model.addAttribute("name", user.getName());
            if (user.getName() != null && !user.getName().isEmpty()) {
                model.addAttribute("nameInitial", String.valueOf(user.getName().charAt(0)).toUpperCase());
            }
            if (user.getPhoto() != null) {
                model.addAttribute("photoId", user.getPhoto().getId());
            }
        }
        List<Post> allPosts = postService.findAll();
        if (!allPosts.isEmpty()) {
            for (Post p : allPosts) {
                p.calculateTime();
            }
            model.addAttribute("list", allPosts);
        }
        return "home";
    }
    

    @GetMapping(value = "/searchUsers", produces = "text/html;charset=UTF-8")
    public String ajaxSearchUsers(
        @RequestParam String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        Model model) {

        String q = name == null ? "" : name.trim();

        List<User> foundUsers = q.isEmpty()
            ? List.of()
            : userService.searchUsersBySimilarName(q, page, size);

        model.addAttribute("foundUsers", foundUsers);

        return "fragments/search-users"; 
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
