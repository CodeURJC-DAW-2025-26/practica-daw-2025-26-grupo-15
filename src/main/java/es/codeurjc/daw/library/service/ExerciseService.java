package es.codeurjc.daw.library.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.repository.ExerciseRepository;
import es.codeurjc.daw.library.model.User;
import org.springframework.web.multipart.MultipartFile;
import es.codeurjc.daw.library.model.ExerciseList;

@Service
public class ExerciseService {
    
    @Autowired 
    private ExerciseRepository exerciseRepo;

    @Autowired 
    private ExerciseListService exerciseListService;

    public Exercise findById(Long id) {
        return exerciseRepo.findById(id).orElseThrow(() -> new RuntimeException("Exercise not found"));
    }

    public Exercise createExercise(Exercise exercise, User user, MultipartFile imageFile, Long listId) throws IOException{
        exercise.setOwner(user);
        exercise.setNumSolutions(0);
        if (exercise.getTitle() == null || exercise.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if(exercise.getDescription() == null || exercise.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
    

        if (imageFile != null && !imageFile.isEmpty()) {
        exercise.setPdfImage(BlobProxy.generateProxy(
            imageFile.getInputStream(), 
            imageFile.getSize()
        ));
        }

         ExerciseList exerciseList = exerciseListService.findById(listId);
        exercise.setExerciseList(exerciseList);
        exerciseList.getExercises().add(exercise);

        exerciseRepo.save(exercise);
        return exercise;
    }
}
