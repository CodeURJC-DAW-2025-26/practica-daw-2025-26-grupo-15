package es.codeurjc.daw.library.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.service.SolutionService; 
import es.codeurjc.daw.library.model.Solution;


@Controller
public class SolutionController {

    @Autowired
    private UserService userService;

    @Autowired
    private SolutionService solutionService;

    @GetMapping("/solution/{id}")
    public String solution(Model model, Principal principal, @PathVariable Long id) {
         if (principal == null) {
            return "redirect:/login";
        }       
        
        User user = resolveUser(principal);
        Solution solution = solutionService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("solution", solution);
        model.addAttribute("exercise", solution.getExercise());
        model.addAttribute("comments", solution.getComments());

        return "solution";
    }

    @GetMapping("/newsolution")
    public String createSolution() {
        return "createSolution";
    }

    @GetMapping("/editsolution")
    public String editSolution() {
        return "editSolution";
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
    
    