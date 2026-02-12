package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExerciseListController {

    @GetMapping("/list-view")
    public String getListView() {
        return "list-view";
    }

    @GetMapping("/exercise")
    public String getExercise() {
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
