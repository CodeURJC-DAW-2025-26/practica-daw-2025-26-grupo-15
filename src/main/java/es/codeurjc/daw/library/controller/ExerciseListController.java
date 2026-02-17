package es.codeurjc.daw.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.ui.Model;
import java.util.List;
import es.codeurjc.daw.library.model.User;

@Controller
public class ExerciseListController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseListService listService;

    @GetMapping("/list-view")
    public String getListView(Model model) {
        User user = userService.findByName("user").orElseThrow(); // cogemos user q de momento es el único q hay
        List<ExerciseList> allLists = listService.findByOwner(user);

        if (!allLists.isEmpty()) {
            model.addAttribute("list", allLists.get(0)); //esta de ejemplo
        }
        model.addAttribute("user", user);
        return "list-view";
    }

    @GetMapping("/exercise")
    public String getExercise(Model model) {
        User user = userService.findByName("user").orElseThrow(); 
        List<ExerciseList> allLists = listService.findByOwner(user);
        model.addAttribute("user", user);
        if (!allLists.isEmpty()) {
            model.addAttribute("list", allLists.get(0)); //esta de ejemplo
            model.addAttribute("exercise", allLists.get(0).getExercises().get(0)); //este ejercicio de ejemplo
        }

        return "exercise";
    }

    @GetMapping("/new-list")
    public String getNewList() {
        return "new-list";
    }

    @GetMapping("/new-exercise")
    public String getNewExercise() {
        return "new-exercise";
    }

}
