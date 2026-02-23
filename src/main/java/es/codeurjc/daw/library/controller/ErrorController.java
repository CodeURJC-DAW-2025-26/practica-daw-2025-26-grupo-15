package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ErrorController {

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/loginerror")
    public String loginError() {
        return "loginerror";
    }
    

}
