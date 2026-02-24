package es.codeurjc.daw.library.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ExerciseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/list-view/{listId}/new-exercise")
    public String showNewExerciseForm(Model model, @PathVariable Long listId) {
        model.addAttribute("listId", listId);

        return "new-exercise";
    }

    @PostMapping("/list-view/{listId}/new-exercise")
    public String addNewExercise(Model model, Exercise newExercise, MultipartFile imageFile, Principal principal,
            @PathVariable Long listId) {

        User user = resolveUser(principal);

        try {
            exerciseService.createExercise(newExercise, user, imageFile, listId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "redirect:/list-view/" + listId;
    }

    @GetMapping("/exercise/{id}")
    public String getExercise(Model model, Principal principal, @PathVariable Long id) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            User user = resolveUser(principal);
            Exercise exercise = exerciseService.findById(id);
            model.addAttribute("user", user);
            model.addAttribute("nameInitial", String.valueOf(user.getName().charAt(0)).toUpperCase());
            model.addAttribute("exercise", exercise);
            model.addAttribute("list", exercise.getExerciseList());
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
        return "exercise";
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
