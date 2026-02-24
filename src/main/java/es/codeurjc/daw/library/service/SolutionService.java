package es.codeurjc.daw.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.daw.library.repository.SolutionRepository;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.model.User;
import java.sql.Date;
import org.springframework.web.multipart.MultipartFile;
import java.sql.Blob;
import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import es.codeurjc.daw.library.model.Exercise;

@Service
public class SolutionService {

    @Autowired
    private SolutionRepository solutionRepo;

    @Autowired
    private EntityManager entityManager;

    @Autowired 
    private ExerciseService exerciseService;


    public Solution findById(Long id) {
        return solutionRepo.findById(id).orElseThrow(() -> new RuntimeException("Solution not found"));
    }


    @jakarta.transaction.Transactional
    public Solution createSolution(Long exerciseId, Solution solution, User user, MultipartFile file) throws Exception {
        solution.setOwner(user);
        solution.setNumComments(0);
        solution.setLastUpdate(new Date(System.currentTimeMillis()));
        if(solution.getName() == null || solution.getName().isEmpty() || solution.getDescription() == null || solution.getDescription().isEmpty()) {
            throw new RuntimeException("Name and description cannot be empty");
        }

        if (file != null && !file.isEmpty()) {
            Session session = entityManager.unwrap(Session.class);
            Blob blob = session.getLobHelper().createBlob(file.getInputStream(), file.getSize());
            solution.setPdfImage(blob);
        }else{
            throw new RuntimeException("File cannot be empty");
        }
    
        Exercise exercise = exerciseService.findById(exerciseId);
        solution.setExercise(exercise);
        exercise.getSolutions().add(solution);
        

    
        return solutionRepo.save(solution);
    }


}
