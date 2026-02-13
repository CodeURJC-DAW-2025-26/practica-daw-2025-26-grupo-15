package es.codeurjc.daw.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.repository.ExerciseListRepository;

@Service
public class ExerciseListService {
    
    @Autowired
    private ExerciseListRepository listRepo;

    public List<ExerciseList> findByOwner(User user){
        return listRepo.findByOwner(user);
    }
}
