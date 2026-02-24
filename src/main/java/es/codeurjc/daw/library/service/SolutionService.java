package es.codeurjc.daw.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.daw.library.repository.SolutionRepository;
import es.codeurjc.daw.library.model.Solution;

@Service
public class SolutionService {

    @Autowired
    private SolutionRepository solutionRepo;

    public Solution findById(Long id) {
        return solutionRepo.findById(id).orElseThrow(() -> new RuntimeException("Solution not found"));
    }
    
}
