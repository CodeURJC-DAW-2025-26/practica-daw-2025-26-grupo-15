package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    
    @GetMapping("/profile")
    public String viewProfile(){
        return "profile";
    }

    @GetMapping("/follow-requests")
    public String viewFollowRequests(){
        return "follow-requests";
    }

    @GetMapping("/following")
    public String viewFollowing(){
        return "following";
    }

    @GetMapping("/edit-profile")
    public String editProfile(){
        return "edit-profile";
    }

    @GetMapping("/admin")
    public String adminPanel(){
        return "admin";
    }
}
