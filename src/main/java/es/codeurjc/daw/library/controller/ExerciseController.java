package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.sql.Blob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class ExerciseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/list-view/{listId}/new-exercise")
    public String showNewExerciseForm(Model model, @PathVariable Long listId) {
        model.addAttribute("listId", listId);
        model.addAttribute("action","/list-view/"+listId+"/new-exercise");
        return "new-exercise";
    }

    @PostMapping("/list-view/{listId}/new-exercise")
    public String addNewExercise(Model model, 
                                 Exercise newExercise, 
                                 @RequestParam(required = false, name="pdfFile") MultipartFile pdfFile, 
                                 Principal principal,
                                 @PathVariable Long listId) {

        User user = resolveUser(principal);

        try {
            exerciseService.createExercise(newExercise, user, pdfFile, listId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "redirect:/list-view/" + listId;
    }

    @GetMapping("/exercise/{id}")
    public String getExercise(Model model, Principal principal, @PathVariable Long id) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            User user = resolveUser(principal);
            Exercise exercise = exerciseService.findById(id);
            model.addAttribute("user", user);
            model.addAttribute("nameInitial", String.valueOf(user.getName().charAt(0)).toUpperCase());
            model.addAttribute("exercise", exercise);
            model.addAttribute("list", exercise.getExerciseList());
            boolean isOwner = exercise.getExerciseList().getOwner().getId().equals(user.getId());
            model.addAttribute("isOwner", isOwner);
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
        return "exercise";
    }

    @GetMapping("/edit-exercise/{exerciseId}")
    public String showEditExerciseForm(Model model, @PathVariable Long exerciseId) {

        Exercise exercise = exerciseService.findById(exerciseId);
        if (exercise == null) {
            model.addAttribute("errorMessage", "Exercise not found");
            return "error";
        }

        model.addAttribute("exercise", exercise);
        model.addAttribute("action", "/edit-exercise/" + exerciseId);
        model.addAttribute("listId", exercise.getExerciseList().getId());

        return "new-exercise";
    }

    @PostMapping("/edit-exercise/{exerciseId}")   
    public String editExercise(Model model,
                               Exercise editedExercise,
                               @RequestParam(required = false, name = "pdfFile") MultipartFile pdfFile,
                               Principal principal,
                               @PathVariable Long exerciseId) {

        User user = resolveUser(principal);

        try {
            exerciseService.updateExercise(exerciseId, editedExercise, user, pdfFile);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "redirect:/exercise/" + exerciseId;
    }

    @GetMapping("/exercise/{exerciseId}/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> downloadExercisePdf(@PathVariable Long exerciseId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        Exercise ex = exerciseService.findById(exerciseId);
        if (ex == null || ex.getPdfImage() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Blob blob = ex.getPdfImage();
            byte[] bytes = blob.getBytes(1, (int) blob.length());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"exercise-" + ex.getTitle() + "-" + ex.getId() + ".pdf\"")
                    .body(bytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    

    private User resolveUser(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            String provider = oauth2Token.getAuthorizedClientRegistrationId();
            String providerId;
            if ("github".equals(provider)) {
                Integer id = oauth2Token.getPrincipal().getAttribute("id");
                providerId = id != null ? id.toString() : null;
            } else {
                providerId = oauth2Token.getPrincipal().getAttribute("sub");
            }
            return userService.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> new RuntimeException("OAuth2 user not found in DB"));
        } else {
            return userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }
}
