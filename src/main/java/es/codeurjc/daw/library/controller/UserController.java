package es.codeurjc.daw.library.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal, @RequestParam String userName) {    
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        if (!user.getName().equals(userName)){
            Optional<User> otherUser = userService.findByName(userName);
            if (otherUser.isPresent())
                user = otherUser.get();
            else 
                throw new RuntimeException("Error finding user " + userName);
        }
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
    public String viewFollowing(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        //TODO: distinguish between principal user and other viewed profile
        User user = resolveUser(principal);

        return "following";
    }

    @GetMapping("/edit-profile")
    public String editProfile() {
        return "edit-profile";
    }

    @GetMapping("/admin")
    public String adminPanel() {
        return "admin";
    }
}
