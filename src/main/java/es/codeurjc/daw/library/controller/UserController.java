package es.codeurjc.daw.library.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        boolean isAuthenticated = principal != null && !(principal instanceof AnonymousAuthenticationToken);
        if (isAuthenticated) {
            model.addAttribute("logged", true);
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping("/profile")
    public String viewOwnProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        List<ExerciseList> userLists = listService.findByOwner(user);
        model.addAttribute("user", user);
        model.addAttribute("userLists", userLists);
        model.addAttribute("isOwnProfile", true);
        return "profile";
    }

    @GetMapping("/profile/{id}")
    public String viewProfile(Model model, Principal principal, @PathVariable Long id) {
        try {
            User profileUser = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<ExerciseList> userLists = listService.findByOwner(profileUser);
            model.addAttribute("user", profileUser);
            model.addAttribute("userLists", userLists);

            if (principal != null) {
                User loggedUser = resolveUser(principal);
                model.addAttribute("isOwnProfile", loggedUser.getId().equals(profileUser.getId()));
                model.addAttribute("loggedUserId", loggedUser.getId());
                model.addAttribute("hasRequested", userService.hasRequestedToFollow(loggedUser, user));
            } else {
                model.addAttribute("isOwnProfile", false);
            }
    } catch (Exception e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }
        return "profile";
    }

    @GetMapping("/follow-requests")
    public String viewFollowRequests(Model model, Principal principal) {
        User user = resolveUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("pendingCount", 0);

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
    public String editProfile(Model model, Principal principal) {
        User user = resolveUser(principal);
        model.addAttribute("user", user);
        if (user.getName() != null && !user.getName().isEmpty()) {
            model.addAttribute("nameInitial", String.valueOf(user.getName().charAt(0)).toUpperCase());
        }
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
        if (user.getName() != null && !user.getName().isEmpty()) {
            model.addAttribute("nameInitial", String.valueOf(user.getName().charAt(0)).toUpperCase());
        }
        return "edit-profile-form";
    }

    @PostMapping("/edit-profile-save")
    public String editProfileSave(Model model, User user,
            @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
            Principal principal) {
        User oldUser = resolveUser(principal);
        User newUser;
        try {
            newUser = userService.modify(user, oldUser, photoFile);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
        
        model.addAttribute("user", newUser);
        return "redirect:/profile";
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
