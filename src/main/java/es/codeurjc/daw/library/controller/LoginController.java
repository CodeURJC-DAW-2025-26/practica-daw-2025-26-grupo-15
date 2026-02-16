package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "sign-in";
    }

    @GetMapping("/register")
    public String register() {
        return "sign-up";
    }
    

    

    
}
