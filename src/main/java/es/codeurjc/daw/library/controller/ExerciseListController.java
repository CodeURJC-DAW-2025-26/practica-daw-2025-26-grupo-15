package es.codeurjc.daw.library.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import es.codeurjc.daw.library.service.ExerciseService;

@Controller
public class ExerciseListController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/list-view/{id}")
    public String getListView(Model model, Principal principal, @PathVariable Long id) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        ExerciseList list = listService.findById(id);

        model.addAttribute("list", list);
        model.addAttribute("user", user);
        return "list-view";
    }

    @GetMapping("/exercise/{id}")
    public String getExercise(Model model, Principal principal, @PathVariable Long id) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        Exercise exercise = exerciseService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("exercise", exercise);
        model.addAttribute("list", exercise.getExerciseList());
    
        return "exercise";
    }

    @GetMapping("/new-list")
    public String getNewList() {
        return "new-list";
    }

    @PostMapping("/add-new-list")
    public String addNewList(Model model, ExerciseList newList, Principal principal) {

        User user = resolveUser(principal);

        try {
            listService.createList(newList, user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "redirect:/profile";
    }

    @GetMapping("/new-exercise")
    public String getNewExercise() {
        return "new-exercise";
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
