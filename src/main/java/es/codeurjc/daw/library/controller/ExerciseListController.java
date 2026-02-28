package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.PostService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExerciseListController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    @Autowired 
    private PostService postService;


    @GetMapping("/list-view/{id}")
    public String getListView(Model model, Principal principal, @PathVariable Long id) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        ExerciseList list = listService.findById(id);

        model.addAttribute("list", list);
        model.addAttribute("user", user);
        model.addAttribute("isOwner", list.getOwner().getId().equals(user.getId()));
        return "list-view";
    }
    

    @GetMapping("/new-list")
    public String getNewList(Model model) {
        model.addAttribute("action", "/add-new-list");
        return "new-list";
    }


    @PostMapping("/edit-list-content/{id}")
    public String editListContent(Model model, @PathVariable Long id, ExerciseList editedList, Principal principal) {
        User user = resolveUser(principal);
        ExerciseList originalList = listService.findById(id);
        try {
            listService.editList(editedList, originalList, user);
            postService.createPost(new Post(
                user,
                editedList.getTitle(),
                "/list-view/"+ editedList.getId(),
            "Edited List"  
            ));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "redirect:/profile";
    }



    @GetMapping("/edit-list/{id}")
    public String getEditList(Model model, @PathVariable Long id, Principal principal) {

        User user = resolveUser(principal);
        ExerciseList list = listService.findById(id);

        model.addAttribute("list", list);
        model.addAttribute("user", user);
        model.addAttribute("action", "/edit-list-content/"+id);

        return "new-list";
    }



    @PostMapping("/add-new-list")
    public String addNewList(Model model, ExerciseList newList, Principal principal) {

        User user = resolveUser(principal);
  
        try {
            listService.createList(newList, user);
            postService.createPost(new Post(
                user,
                newList.getTitle(),
                "/list-view/"+ newList.getId(),
            "New List"  
            ));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "redirect:/profile";
    }



    @PostMapping("delete/list/{id}")
    public String deleteList(Model model, @PathVariable Long id, Principal principal) {
        User user = resolveUser(principal);

        ExerciseList list = listService.findById(id);

        try{
            listService.deleteList(list, user);
        } catch (SecurityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
     
        return "redirect:/profile";
    }



    @GetMapping("/searchLists")
    public String searchLists(@RequestParam int page,
                              @RequestParam int size,
                              @RequestParam Long userId,
                              Model model,
                              Principal principal,
                              HttpServletResponse response) {;
        Optional<User> opt = userService.findById(userId);
        if (opt.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = opt.get();
        Slice<ExerciseList> slice = listService.findByOwner(user, page, size);

        response.setHeader("X-Has-More", String.valueOf(slice.hasNext()));
        response.setHeader("X-Results-Count", String.valueOf(slice.getNumberOfElements()));

        model.addAttribute("userLists", slice.getContent());
        model.addAttribute("isOwnProfile", user.equals(resolveUser(principal)));
        
        return "fragments/search-lists";
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
