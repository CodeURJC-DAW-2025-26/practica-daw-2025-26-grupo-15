package es.codeurjc.daw.library.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Model model) {

        if (!request.isUserInRole("ADMIN")) {
            throw new RuntimeException("Access denied");
        }

        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        return switch (petition) {
            case "au" -> {
                Slice<User> slice;
                if (inputFilter == null || inputFilter.isEmpty()) slice = userService.findAll(page, size);
                else slice = userService.searchUsersBySimilarName(inputFilter, page, size);
                this.setSliceHeaders(response, slice);
                model.addAttribute("allUsers", slice.getContent());
                yield "fragments/admin-search-users";
            }
            case "al" -> {
                Slice<ExerciseList> slice;
                if (inputFilter == null || inputFilter.isEmpty()) slice = listService.findAll(page, size);
                else slice = listService.searchListsBySimilarTitle(inputFilter, page, size);
                this.setSliceHeaders(response, slice);
                model.addAttribute("allLists", slice.getContent());
                yield "fragments/admin-search-lists";
            }
            case "ae" -> {
                Slice<Exercise> slice;
                if (inputFilter == null || inputFilter.isEmpty()) slice = exerciseService.findAll(page, size);
                else slice = exerciseService.searchExercisesBySimilarTitle(inputFilter, page, size);
                this.setSliceHeaders(response, slice);
                model.addAttribute("allExercises", slice.getContent());
                yield "fragments/admin-search-exercises";
            }
            default -> throw new IllegalArgumentException("Invalid operation: " + petition);
        };
    }

    private void setSliceHeaders(HttpServletResponse response, Slice<?> slice) {
        response.setHeader("X-Has-More", String.valueOf(slice.hasNext()));
        response.setHeader("X-Results-Count", String.valueOf(slice.getNumberOfElements()));
    }


    @GetMapping("/loadModals/{petition}/{amount}")
    public String loadModals(@PathVariable String petition,
                             @PathVariable int amount,
                             Model model) {
        switch (petition){
            case "au":
                Slice<User> sliceUsers = userService.findAll(0, amount);
                model.addAttribute("users", sliceUsers.getContent());
                break;
            case "al":
                Slice<ExerciseList> sliceLists = listService.findAll(0, amount);
                model.addAttribute("lists", sliceLists.getContent()); 
                break;
            case "ae":
                Slice<Exercise> sliceExercises = exerciseService.findAll(0, amount);
                model.addAttribute("exercises", sliceExercises.getContent());  
                break;     
            default:
                throw new RuntimeException("Invalid operation");    
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
