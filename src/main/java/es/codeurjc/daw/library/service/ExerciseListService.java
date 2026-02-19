package es.codeurjc.daw.library.service;

import java.sql.Date;
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

    public ExerciseList createList(ExerciseList list, User owner){
        list.setOwner(owner);
        list.setLastUpdate(new Date(System.currentTimeMillis()));

        if (list.getTitle() == null || list.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        return listRepo.save(list);
    }

}
