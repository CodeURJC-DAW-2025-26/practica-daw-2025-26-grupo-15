package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.service.admin.AdminService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private ExerciseListService listService;
    @Autowired
    private ExerciseService exerciseService;

    @FunctionalInterface
    private interface AdminSearchHandler {
        Slice<?> handle(int page, int size, String inputFilter);
    }
    
    @Autowired
    private AdminService adminService;

    private Map<String, AdminSearchHandler> handlers;

    @PostConstruct
    private void initHandlers() {
        handlers = Map.of(
            "au", adminService::searchUsers,
            "al", adminService::searchLists,
            "ae", adminService::searchExercises
        );
    }

    private final Map<String, String> viewByPetition = Map.of(
        "au", "fragments/admin-search-users",
        "al", "fragments/admin-search-lists",
        "ae", "fragments/admin-search-exercises"
    );
    

    @GetMapping("/admin")
    public String adminPanel(Model model, Principal principal) {
        if (principal != null) {
            User current = resolveUser(principal);
            model.addAttribute("currentUser", current);
        }
        java.util.List<User> all = userService.findAll();
        model.addAttribute("allUsers", all);
        model.addAttribute("userCount", all.size());
        return "admin";
    }

    @GetMapping("/adminSearch")
    public String adminSearch(@RequestParam int page,
                            @RequestParam int size,
                            @RequestParam String petition,
                            @RequestParam(required = false) String inputFilter,
                            HttpServletResponse response,
                            Model model) {

        AdminSearchHandler handler = handlers.get(petition);
        String view = viewByPetition.get(petition);

        if (handler == null || view == null) {
            throw new IllegalArgumentException("Invalid operation: " + petition);
        }

        Slice<?> slice = handler.handle(page, size, inputFilter);

        response.setHeader("X-Has-More", String.valueOf(slice.hasNext()));
        response.setHeader("X-Results-Count", String.valueOf(slice.getNumberOfElements()));

        model.addAttribute("elems", slice.getContent());
        return view;
    }

    @GetMapping("/loadModals")
    public String loadModals(@RequestParam String petition,
                            @RequestParam(name = "ids") List<Long> ids,
                            Model model) {

        if (ids == null || ids.isEmpty()) {
            return "fragments/admin-modals";
        }

        switch (petition) {
            case "au" -> model.addAttribute("users", userService.findAllById(ids));
            case "al" -> model.addAttribute("lists", listService.findAllById(ids));
            case "ae" -> model.addAttribute("exercises", exerciseService.findAllById(ids));
            default -> throw new IllegalArgumentException("Invalid operation: " + petition);
        }

        return "fragments/admin-modals";
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
