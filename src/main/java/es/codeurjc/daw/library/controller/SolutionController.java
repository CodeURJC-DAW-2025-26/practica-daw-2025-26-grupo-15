package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.text.Normalizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.service.SolutionService; 
import es.codeurjc.daw.library.service.SolutionPdfExportService;
import es.codeurjc.daw.library.model.Solution;



@Controller
public class SolutionController {

    @Autowired
    private UserService userService;

    @Autowired
    private SolutionService solutionService;

    @Autowired
    private SolutionPdfExportService solutionPdfExportService;

    @GetMapping("/solution/{id}")
    public String solution(Model model, Principal principal, @PathVariable Long id) {
        Solution solution = solutionService.findById(id);
        model.addAttribute("solution", solution);
        model.addAttribute("exercise", solution.getExercise());
        model.addAttribute("comments", solution.getComments());
        model.addAttribute("logged", principal != null);
        model.addAttribute("isOwner", false);

        if (principal != null) {
            User user = resolveUser(principal);
            model.addAttribute("user", user);
            model.addAttribute("nameInitial", String.valueOf(user.getName().charAt(0)).toUpperCase());
            boolean isOwner = solution.getOwner().getId().equals(user.getId());
            model.addAttribute("isOwner", isOwner);
        }

        return "solution";
    }


    @GetMapping("/add-solution/{exerciseId}")
    public String createSolution(@PathVariable Long exerciseId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = resolveUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("exerciseId", exerciseId);
        return "add-solution";
    }

    @PostMapping("/exercise/{exerciseId}/new-solution")
    public String newSolution(Model model,@PathVariable Long exerciseId, Solution solution, Principal principal, @RequestParam("imageFile") MultipartFile file) {
    
        User user = resolveUser(principal);
        try {
            solutionService.createSolution(exerciseId, solution, user, file);
        } catch (Exception e) {
            model.addAttribute("errorMessage",e.getMessage());
            return "error";
        }
        
        return "redirect:/exercise/" + exerciseId;
    }


    @PostMapping("/exercise/{exerciseId}/solution/{solutionId}/delete")
    public String deleteSolution(Model model, @PathVariable Long exerciseId, @PathVariable Long solutionId, Principal principal) {
        User user = resolveUser(principal);
        
        try {
            solutionService.deleteSolution(solutionId, user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
        return "redirect:/exercise/" + exerciseId;
    }

    @GetMapping("/solution/{id}/export/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> exportSolutionPdf(@PathVariable Long id, Principal principal) {
        try {
            Solution solution = solutionService.findById(id);
            byte[] pdf = solutionPdfExportService.generateSolutionPdf(solution);
            String safeName = sanitizeFileName(solution.getName());
            String fileName = "solution-" + solution.getId() + "-" + safeName + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(pdf);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Solution not found")) {
                return ResponseEntity.notFound().build();
            }
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

    private String sanitizeFileName(String rawTitle) {
        if (rawTitle == null || rawTitle.trim().isEmpty()) {
            return "untitled";
        }

        String normalized = Normalizer.normalize(rawTitle, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String slug = normalized.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        return slug.isEmpty() ? "untitled" : slug;
    }


}
    
    
