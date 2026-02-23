package es.codeurjc.daw.library.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.repository.ExerciseRepository;

@Service
public class ExerciseService {
    
    @Autowired 
    private ExerciseRepository exerciseRepo;

    public Exercise findById(Long id) {
        return exerciseRepo.findById(id).orElseThrow(() -> new RuntimeException("Exercise not found"));
    }
}
