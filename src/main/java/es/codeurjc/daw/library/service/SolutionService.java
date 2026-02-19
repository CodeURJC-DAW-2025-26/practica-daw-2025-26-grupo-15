package es.codeurjc.daw.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.daw.library.repository.SolutionRepository;
import es.codeurjc.daw.library.model.Solution;
import java.util.Optional;

@Service
public class SolutionService {

    @Autowired
    private SolutionRepository solutionRepo;

    public Optional<Solution> findById(Long id) {
        return solutionRepo.findById(id);
    }
    
}
