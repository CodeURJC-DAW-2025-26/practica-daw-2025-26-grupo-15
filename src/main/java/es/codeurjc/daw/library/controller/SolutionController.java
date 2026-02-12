package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class SolutionController {

    @GetMapping("/solution")
    public String solution() {
        return "solution";
    }

    @GetMapping("/newsolution")
    public String createSolution() {
        return "createSolution";
    }

    @GetMapping("/editsolution")
    public String editSolution() {
        return "editSolution";
    }
    


}
    
    