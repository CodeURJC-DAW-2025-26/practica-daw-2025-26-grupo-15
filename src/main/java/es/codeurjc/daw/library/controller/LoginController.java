package es.codeurjc.daw.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        return "sign-in";
    }

    @GetMapping("/register")
    public String register(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        return "sign-up";
    }

    @PostMapping("/form-register")
    public String createUser(User user, Model model, HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        try {
            userService.register(user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "error";
        }
        return "redirect:/login";
    }
}
