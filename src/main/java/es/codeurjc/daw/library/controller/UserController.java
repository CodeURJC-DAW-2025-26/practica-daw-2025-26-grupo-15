package es.codeurjc.daw.library.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.daw.library.repository.ExerciseListRepository;

import org.springframework.ui.Model;
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
    public String viewProfile(Model model) {
        User user = userService.findByName("user").orElseThrow(); //cogemos user q de momento es el único q hay
        List<ExerciseList> userLists = listService.findByOwner(user);
        model.addAttribute("user", user);
        model.addAttribute("userLists", userLists);
        //tb iría aquí las request
        return "profile";
    }

    // clase aux para las solicitudes
    public class FollowRequest {
        private String name;

        public FollowRequest(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
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
    public String editProfile() {
        return "edit-profile";
    }

    @GetMapping("/admin")
    public String adminPanel() {
        return "admin";
    }
}
