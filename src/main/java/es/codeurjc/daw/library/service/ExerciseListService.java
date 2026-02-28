package es.codeurjc.daw.library.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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

    public Slice<ExerciseList> findByOwner(User user, int page, int size){
        return listRepo.findByOwner(user, PageRequest.of(page, size));
    }

    public Slice<ExerciseList> findAll(int page, int size){
        return listRepo.findAll(PageRequest.of(page, size));
    }

    public Slice<ExerciseList> searchListsBySimilarTitle(String q, int page, int size) {
        return listRepo.searchListsBySimilarTitle(q, PageRequest.of(page, size));
    }

    public List<ExerciseList> findAllById(List<Long> ids){
        return listRepo.findAllById(ids);
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

    public void deleteList (ExerciseList list, User user){
        if (!list.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("User is not the owner of the list");
        }
        listRepo.deleteById(list.getId());
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
