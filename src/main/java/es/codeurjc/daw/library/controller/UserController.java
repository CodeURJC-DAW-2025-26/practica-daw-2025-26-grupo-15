package es.codeurjc.daw.library.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.UserService;

@Controller
public class UserController {
    //esto inyecta los repos aquípara poder usarlos en los métodos
    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        List<ExerciseList> userLists = listService.findByOwner(user);
        model.addAttribute("user", user);
        model.addAttribute("userLists", userLists);
        return "profile";
    }
    private User resolveUser(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            String provider = oauth2Token.getAuthorizedClientRegistrationId();
            
            // Different providers use different attribute names
            String providerId;
            if ("github".equals(provider)) {
                // GitHub uses 'id' instead of 'sub'
                Integer id = oauth2Token.getPrincipal().getAttribute("id");
                providerId = id != null ? id.toString() : null;
            } else {
                // Google and other OIDC providers use 'sub'
                providerId = oauth2Token.getPrincipal().getAttribute("sub");
            }
            
            return userService.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> new RuntimeException("OAuth2 user not found in DB"));
        } else {
            // Form login — principal.getName() is the username
            return userService.findByName(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }


    @GetMapping("/follow-requests")
    public String viewFollowRequests() {
        return "follow-requests";
    }

    @GetMapping("/following")
    public String viewFollowing() {
        return "following";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model, Principal principal) {
        User user = resolveUser(principal);
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @GetMapping("/admin")
    public String adminPanel() {
        return "admin";
    }

    @GetMapping("/edit-profile-form")
    public String editProfileForm(Model model, Principal principal) {
        User user = resolveUser(principal);
        model.addAttribute("user", user);
        return "edit-profile-form";
    }

    @PostMapping("/edit-profile-save")
    public String editProfileSave(Model model, User user , Principal principal) {

        User oldUser = resolveUser(principal);
        User newUser;
        try {
            newUser = userService.modify(user, oldUser);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
        
        model.addAttribute("user", newUser);
        return "profile";
    }
}
