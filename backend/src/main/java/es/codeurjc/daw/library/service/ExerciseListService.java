package es.codeurjc.daw.library.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public ExerciseList deleteById(long id) {

        ExerciseList list = listRepo.findById(id).orElseThrow();

        listRepo.deleteById(id);

        return list;
    }

    public Page<ExerciseList> findByOwner(User user, Pageable pageable){
        return listRepo.findByOwner(user, pageable);
    }

    public Page<ExerciseList> findAll(Pageable pageable){
        return listRepo.findAll(pageable);
    }

    public Page<ExerciseList> searchListsBySimilarTitle(String q, Pageable pageable) {
        return listRepo.searchListsBySimilarTitle(q, pageable);
    }

    public List<ExerciseList> findAllById(List<Long> ids){
        return listRepo.findAllById(ids);
    }

    public ExerciseList editList(ExerciseList editedList, ExerciseList originalList, User user) {
        if (!originalList.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("You are not allowed to edit this list");
        }

        if (editedList.getTitle() == null || editedList.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (editedList.getTopic() == null || editedList.getTopic().trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be null or empty");
        }

        if (editedList.getDescription() == null || editedList.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        String normalizedTitle = editedList.getTitle().trim();
        String normalizedTopic = editedList.getTopic().trim();
        String normalizedDescription = editedList.getDescription().trim();

        if (normalizedTitle.length() < 3 || normalizedTitle.length() > 100) {
            throw new IllegalArgumentException("The list title must be between 3 and 100 characters.");
        }
        if (normalizedTopic.length() < 3 || normalizedTopic.length() > 100) {
            throw new IllegalArgumentException("The list topic must be between 3 and 100 characters.");
        }
        if (normalizedDescription.length() < 10 || normalizedDescription.length() > 10000) {
            throw new IllegalArgumentException("The list description must be between 10 and 10k characters.");
        }

        originalList.setTitle(normalizedTitle);
        originalList.setTopic(normalizedTopic);
        originalList.setDescription(normalizedDescription);

        return listRepo.save(originalList);

    }

    public void deleteList (ExerciseList list, User user, boolean isAdmin){
        if (!list.getOwner().getId().equals(user.getId()) && !isAdmin) {
            throw new SecurityException("You are not allowed to delete this list");
        }
        listRepo.deleteById(list.getId());
    }

    public ExerciseList createList(ExerciseList list, User owner){
        list.setOwner(owner);
        list.setLastUpdate(new Date(System.currentTimeMillis()));

        if (list.getTitle() == null || list.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (list.getTopic() == null || list.getTopic().trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be null or empty");
        }
        if (list.getDescription() == null || list.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        String normalizedTitle = list.getTitle().trim();
        String normalizedTopic = list.getTopic().trim();
        String normalizedDescription = list.getDescription().trim();

        if (normalizedTitle.length() < 3 || normalizedTitle.length() > 100) {
            throw new IllegalArgumentException("The list title must be between 3 and 100 characters.");
        }
        if (normalizedTopic.length() < 3 || normalizedTopic.length() > 100) {
            throw new IllegalArgumentException("The list topic must be between 3 and 100 characters.");
        }
        if (normalizedDescription.length() < 10 || normalizedDescription.length() > 10000) {
            throw new IllegalArgumentException("The list description must be between 10 and 10k characters.");
        }

        list.setTitle(normalizedTitle);
        list.setTopic(normalizedTopic);
        list.setDescription(normalizedDescription);

        return listRepo.save(list);
    }

    public ExerciseList findById(Long id) {
        return listRepo.findById(id).orElseThrow(() -> new RuntimeException("List not found"));
    }

}
