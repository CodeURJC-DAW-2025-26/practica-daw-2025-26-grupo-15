package es.codeurjc.daw.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.service.PostService;


@Controller
public class WebController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String home(Model model) {
        List<Post> allPosts = postService.findAll();
        
         if (!allPosts.isEmpty()) {
            model.addAttribute("list", allPosts); //esta de ejemplo
        }
        return "home";
    }
    
    
    
}
