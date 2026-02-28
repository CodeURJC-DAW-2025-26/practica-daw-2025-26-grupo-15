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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Slice;



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
            List<UserService.UserPair> suggestions = userService.getFollowingSuggestions(user); //TODO: de momento lo hago así pero podríamos hacerlo con pages yo creo y scroll
            model.addAttribute("suggestions", suggestions);
            if (user.getName() != null && !user.getName().isEmpty()) {
                model.addAttribute("nameInitial", String.valueOf(user.getName().charAt(0)).toUpperCase());
            }
            if (user.getPhoto() != null) {
                model.addAttribute("photoId", user.getPhoto().getId());
            }
        }
        return "home";
    }
    


    @GetMapping(value="/searchUsers", produces="text/html;charset=UTF-8")
    public String searchUsers(@RequestParam String name,
                              @RequestParam(required = false) int page,
                              @RequestParam(required = false) int size,
                                Model model,
                                HttpServletResponse response) {

        String q = name == null ? "" : name.trim();

        if (q.isEmpty()) {
            response.setHeader("X-Has-More", "false");
            response.setHeader("X-Results-Count", "0");
            model.addAttribute("foundUsers", List.of());
            return "fragments/search-users";
        }

        Slice<User> slice = userService.searchUsersBySimilarName(q, page, size);

        response.setHeader("X-Has-More", String.valueOf(slice.hasNext()));
        response.setHeader("X-Results-Count", String.valueOf(slice.getNumberOfElements()));

        model.addAttribute("foundUsers", slice.getContent());

        return "fragments/search-users";
    }

    @GetMapping("/searchPosts")
    public String searchPostsForUser(@RequestParam int page, 
                                     @RequestParam int size, 
                                     Principal principal, 
                                     Model model, 
                                     HttpServletResponse response){
        Slice<Post> slice;
        if (principal != null){
            User user = resolveUser(principal);

            slice = postService.findFeedForUser(user, page, size);
        } else {
            slice = postService.findAll(page, size);
        }
        if (slice == null) throw new RuntimeException("Error on posts search");

        response.setHeader("X-Has-More", String.valueOf(slice.hasNext()));
        response.setHeader("X-Results-Count", String.valueOf(slice.getNumberOfElements()));

        model.addAttribute("list", slice.getContent());
        
        return "fragments/search-posts";
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
