package es.codeurjc.daw.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.PostService;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class WebController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        List<Post> allPosts = postService.findAll();
        
         if (!allPosts.isEmpty()) {
            for (Post p : allPosts){
                p.calculateTime();
            }
            model.addAttribute("list", allPosts); //esta de ejemplo
        }

        return "home";
    }
    

    @GetMapping(value = "/searchUsers", produces = "text/html;charset=UTF-8")
    public String ajaxSearchUsers(
        @RequestParam String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        Model model) {

            System.out.println("hola");

    String q = name == null ? "" : name.trim();

    List<User> foundUsers = q.isEmpty()
            ? List.of()
            : userService.searchUsersBySimilarName(q, page, size);

    model.addAttribute("foundUsers", foundUsers);

    return "fragments/search-users"; // templates/fragments/searchUsers.mustache
}
}
