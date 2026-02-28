package es.codeurjc.daw.library.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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

    @PostMapping("/declineRequest/{fromUser}")
    public String declineRequest(@PathVariable Long fromUser, @RequestParam String srcPage, Model model, Principal principal) {
        User currentUser = resolveUser(principal);

        try{
            userService.declineFollowRequest(currentUser, fromUser);
            return "redirect:" + srcPage;
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/acceptRequest/{fromUser}")
    public String acceptRequest(@PathVariable Long fromUser, @RequestParam String srcPage, Model model, Principal principal) {
        
        User currentUser = resolveUser(principal);

        try{
            userService.acceptFollowRequest(currentUser, fromUser);
            return "redirect:" + srcPage;
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/profile")
    public String viewOwnProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        List<ExerciseList> userLists = listService.findByOwner(user);

        List<User> requests = user.getRequestReceived();
        model.addAttribute("firstTreeRequests", requests.size() > 3 ? requests.subList(0, 3) : requests);
        model.addAttribute("user", user);
        model.addAttribute("followersNumber", user.getFollowers().size());
        model.addAttribute("followingNumber", user.getFollowing().size());
        model.addAttribute("userLists", userLists);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("hasPendingRequests", !requests.isEmpty());
        model.addAttribute("pendingCount", requests.size());
        return "profile";
    }

    @GetMapping("/followers")
    public String viewFollowers(Model model, Principal principal) {
        try {
            User profileUser = resolveUser(principal);
            model.addAttribute("user", profileUser);
            model.addAttribute("followers", profileUser.getFollowers());
            return "followers";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/following")
    public String viewFollowing(Model model, Principal principal) {
        try {
            User profileUser = resolveUser(principal);
            model.addAttribute("user", profileUser);
            model.addAttribute("followers", profileUser.getFollowing());
            return "following";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/profile/{id}")
    public String viewProfile(Model model, Principal principal, @PathVariable Long id) {
        try {
            User profileUser = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<ExerciseList> userLists = listService.findByOwner(profileUser);
            model.addAttribute("user", profileUser);
            model.addAttribute("followersNumber", profileUser.getFollowers().size());
            model.addAttribute("followingNumber", profileUser.getFollowing().size());
            model.addAttribute("userLists", userLists);

            if (principal != null) {
                User loggedUser = resolveUser(principal);
                Boolean isOwnProfile = loggedUser.getId().equals(profileUser.getId());
                model.addAttribute("isOwnProfile", isOwnProfile);
                model.addAttribute("loggedUserId", loggedUser.getId());
                if (!isOwnProfile){
                    User targetUser = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
                     model.addAttribute("hasRequested", userService.hasRequestedToFollow(loggedUser, targetUser));
                     boolean isFollowing = loggedUser.getFollowing().contains(targetUser);
                     model.addAttribute("showFollowButton", !isFollowing && !isOwnProfile);
            } else {
                }
                model.addAttribute("isOwnProfile", false);
            }
    } catch (Exception e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }
        return "profile";
    }

    @PostMapping("/requestToFollow")
    public String requestToFollow(@RequestParam Long requesterId, @RequestParam Long targetId, Model model){
        try{
            User requesterUser = userService.findById(requesterId).orElseThrow(() -> new RuntimeException("User not found"));
            User targetUser = userService.findById(targetId).orElseThrow(() -> new RuntimeException("User not found"));

            userService.requestToFollow(requesterUser, targetUser);
            return "redirect:/profile/" + targetId;
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/follow-requests")
    public String viewFollowRequests(Model model, Principal principal) {
        User user = resolveUser(principal);
        List<User> requests = user.getRequestReceived();
        model.addAttribute("followRequests", requests);
        model.addAttribute("pendingCount", requests.size());
        model.addAttribute("user", user);
        model.addAttribute("followersNumber", user.getFollowers().size());
        model.addAttribute("followingNumber", user.getFollowing().size());
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("pendingCount", 0);

        return "follow-requests";
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

    @PostMapping("/delete-profile/{id}")    
    public String removeUser(Model model, HttpServletRequest request,@PathVariable long id){ 
        try {
            User requester = resolveUser(request.getUserPrincipal());
            boolean isAdmin = request.isUserInRole("ADMIN");


            userService.deleteUser(requester, id, isAdmin);

            if (requester.getId() == id) {
                request.getSession().invalidate();
            }
            if(isAdmin)
                return "redirect:/admin";
            else
                return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
       
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
