package es.codeurjc.daw.library.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/v1/admin/")
public class AdminRestController {

    @GetMapping("/")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    
}
