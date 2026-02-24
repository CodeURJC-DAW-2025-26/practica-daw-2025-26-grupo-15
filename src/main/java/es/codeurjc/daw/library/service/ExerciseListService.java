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

    public ExerciseList editList(ExerciseList editedList, ExerciseList originalList, User user) {
        if (editedList.getTitle() == null || editedList.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (editedList.getTopic() == null || editedList.getTopic().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be null or empty");
        }

        if (editedList.getDescription() == null || editedList.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        originalList.setTitle(editedList.getTitle());
        originalList.setTopic(editedList.getTopic());
        originalList.setDescription(editedList.getDescription());

        return listRepo.save(originalList);

    }

    public ExerciseList createList(ExerciseList list, User owner){
        list.setOwner(owner);
        list.setLastUpdate(new Date(System.currentTimeMillis()));

        if (list.getTitle() == null || list.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        return listRepo.save(list);
    }

    public ExerciseList findById(Long id) {
        return listRepo.findById(id).orElseThrow(() -> new RuntimeException("List not found"));
    }

}
