package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ExerciseListController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    @GetMapping("/list-view")
    public String getListView(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        List<ExerciseList> allLists = listService.findByOwner(user);

        if (!allLists.isEmpty()) {
            model.addAttribute("list", allLists.get(0));
        }
        model.addAttribute("user", user);
        return "list-view";
    }

    @GetMapping("/exercise")
    public String getExercise(Model model) {
        User user = userService.findByEmail("user@example.com").orElseThrow(); 
        List<ExerciseList> allLists = listService.findByOwner(user);
        model.addAttribute("user", user);
        if (!allLists.isEmpty()) {
            model.addAttribute("list", allLists.get(0)); //esta de ejemplo
            model.addAttribute("exercise", allLists.get(0).getExercises().get(0)); //este ejercicio de ejemplo
        }

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
