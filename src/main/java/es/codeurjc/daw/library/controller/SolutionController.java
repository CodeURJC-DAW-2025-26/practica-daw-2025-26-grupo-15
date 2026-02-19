package es.codeurjc.daw.library.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.service.ExerciseListService; 


@Controller
public class SolutionController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    @GetMapping("/solution")
    public String solution(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        List<ExerciseList> allLists = listService.findByOwner(user);

        if (!allLists.isEmpty()) {
            model.addAttribute("solution", allLists.get(0).getExercises().get(0).getSolutions().get(0)); 
            model.addAttribute("comments", allLists.get(0).getExercises().get(0).getSolutions().get(0).getComments());
        }

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
            String providerId = oauth2Token.getPrincipal().getAttribute("sub");
            return userService.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> new RuntimeException("OAuth2 user not found in DB"));
        } else {
            return userService.findByName(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }


}
    
    