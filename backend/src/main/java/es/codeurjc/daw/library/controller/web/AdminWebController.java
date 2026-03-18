package es.codeurjc.daw.library.controller.web;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.service.SearchService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;

@Controller
public class AdminWebController {

    @Autowired
    private UserService userService;
    @Autowired
    private ExerciseListService listService;
    @Autowired
    private ExerciseService exerciseService;

     @FunctionalInterface
    private interface AdminSearchHandler {
        Page<?> handle(Pageable pageable, String inputFilter, Long currentUserId);
    }

    private Map<String, AdminSearchHandler> handlers;
    
    @PostConstruct
    public void initializeHandlers(){
        handlers = Map.of(
            "u", searchService::searchUsers,
            "l", searchService::searchLists,
            "e", searchService::searchExercises,
            "p", searchService::searchPosts
        );
    }
    

    
    @Autowired
    private SearchService searchService;

    // Contains the respective fragments to load the content for each search petition
    private final Map<String, String> viewByPetition = Map.of(
        "u", "fragments/admin-search-users",
        "l", "fragments/admin-search-lists",
        "e", "fragments/admin-search-exercises"
    );
    

    @GetMapping("/admin")
    public String adminPanel(Model model, Principal principal) {
        if (principal != null) {
            User current = resolveUser(principal);
            model.addAttribute("currentUser", current);
        }
        return "admin";
    }

    @GetMapping("/adminSearch")
    public String adminSearch(Pageable pageable,
                            @RequestParam String petition,
                            @RequestParam(required = false) String inputFilter,
                            HttpServletResponse response,
                            Model model,
                            Principal principal) {

        
        String view = viewByPetition.get(petition);

        if (view == null) {
            throw new IllegalArgumentException("Invalid operation: " + petition);
        }

        Long currentUserId = (principal == null)? null : resolveUser(principal).getId();
        //  get the handler for the respective petition and execute to get the Slice
        Page<?> slice = (petition.equals("u"))
            ? this.handlers.get(petition).handle(pageable, inputFilter, currentUserId)
            : this.handlers.get(petition).handle(pageable, inputFilter, null);

        response.setHeader("X-Has-More", String.valueOf(slice.hasNext()));
        response.setHeader("X-Results-Count", String.valueOf(slice.getNumberOfElements()));
        //  add slice content to mustache attribute
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
            case "u" -> model.addAttribute("users", userService.findAllById(ids));
            case "l" -> model.addAttribute("lists", listService.findAllById(ids));
            case "e" -> model.addAttribute("exercises", exerciseService.findAllById(ids));
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
