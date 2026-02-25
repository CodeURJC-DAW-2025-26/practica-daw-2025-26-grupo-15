package es.codeurjc.daw.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.repository.SolutionRepository;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.model.User;
import java.sql.Date;
import org.springframework.web.multipart.MultipartFile;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.Image;

@Service
public class SolutionService {

    @Autowired
    private SolutionRepository solutionRepo;

    @Autowired
    private ImageService imageService;

    @Autowired 
    private ExerciseService exerciseService;


    public Solution findById(Long id) {
        return solutionRepo.findById(id).orElseThrow(() -> new RuntimeException("Solution not found"));
    }


    public Solution createSolution(Long exerciseId, Solution solution, User user, MultipartFile file) throws Exception {
        solution.setOwner(user);
        solution.setNumComments(0);
        solution.setLastUpdate(new Date(System.currentTimeMillis()));
        if(solution.getName() == null || solution.getName().isEmpty() || solution.getDescription() == null || solution.getDescription().isEmpty()) {
            throw new RuntimeException("Name and description cannot be empty");
        }
        if (file == null)
            throw new RuntimeException("File cannot be null");

        Image image = imageService.createImage(file.getInputStream());
        solution.setSolImage(image);
        Exercise exercise = exerciseService.findById(exerciseId);
        solution.setExercise(exercise);
        exercise.getSolutions().add(solution);
        exercise.incrementNumSolutions();
        return solutionRepo.save(solution);
    }


    public void deleteSolution(Long id, User user){
        Solution solution = solutionRepo.findById(id).orElseThrow(() -> new RuntimeException("Solution not found"));
        if (!solution.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this solution");
        }
        solutionRepo.delete(solution);
    }


}
