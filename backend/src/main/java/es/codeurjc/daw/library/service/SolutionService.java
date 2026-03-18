package es.codeurjc.daw.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.repository.SolutionRepository;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.model.User;

import java.io.IOException;
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
        return solutionRepo.findById(id).orElseThrow();
    }


    public Solution createSolution(Long exerciseId, Solution solution, User user, MultipartFile file) throws Exception {
        solution.setOwner(user);
        solution.setNumComments(0);
        solution.setLastUpdate(new Date(System.currentTimeMillis()));
        if(solution.getName() == null || solution.getName().isEmpty() || solution.getDescription() == null || solution.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Name and description cannot be empty");
        }
        if (solution.getName().length() < 3 || solution.getName().length() > 100)
            throw new IllegalArgumentException("The solution name must be between 3 and 100 characters.");
        if (file == null)
            throw new IllegalArgumentException("File cannot be null");
        if(solution.getDescription().length() < 10 || solution.getDescription().length() > 10000)
            throw new IllegalArgumentException("The solution description must be between 10 and 10k characters.");

        Image image = imageService.createImage(file.getInputStream());
        solution.setSolImage(image);
        Exercise exercise = exerciseService.findById(exerciseId);
        solution.setExercise(exercise);
        exercise.getSolutions().add(solution);
        exercise.incrementNumSolutions();
        return solutionRepo.save(solution);
    }

    public Solution createSolutionWithoutImage(Long exerciseId, Solution solution, User user)  {
        solution.setOwner(user);
        solution.setNumComments(0);
        solution.setLastUpdate(new Date(System.currentTimeMillis()));
        if(solution.getName() == null || solution.getName().isEmpty() || solution.getDescription() == null || solution.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Name and description cannot be empty");
        }
        if (solution.getName().length() < 3 || solution.getName().length() > 100)
            throw new IllegalArgumentException("The solution name must be between 3 and 100 characters.");
        Exercise exercise = exerciseService.findById(exerciseId);
        solution.setExercise(exercise);
        exercise.getSolutions().add(solution);
        exercise.incrementNumSolutions();
        return solutionRepo.save(solution);
    }


    public void deleteSolution(Long id, User user, boolean isAdmin){
        Solution solution = solutionRepo.findById(id).orElseThrow();
        if (!solution.getOwner().getId().equals(user.getId()) && !isAdmin) {
            throw new SecurityException("You do not have permission to delete this solution");
        }
        solution.getExercise().decrementNumSolutions();
        solutionRepo.delete(solution);
        
    }

    public Solution addPhotoToSolution(Long id, MultipartFile imageFile, User user) {
        Solution solution = solutionRepo.findById(id).orElseThrow();
        if (!solution.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("You do not have permission to edit this solution");
        }
        
        try{
            Image image = imageService.createImage(imageFile.getInputStream());
            solution.setSolImage(image);
            solutionRepo.save(solution);
            return solution;
        }catch(IOException e){
            throw new RuntimeException("Error processing the image file");
        }
    }

}
