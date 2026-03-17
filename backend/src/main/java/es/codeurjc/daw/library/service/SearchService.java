package es.codeurjc.daw.library.service;


import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.Post;


@Service
public class SearchService {

    @Autowired private PostService postService;
    @Autowired private UserService userService;
    @Autowired private ExerciseListService listService;
    @Autowired private ExerciseService exerciseService;
    

    
    public Page<User> searchUsers(Pageable pageable, String filter, Long currentUserId) {
        if (currentUserId == null) 
            return (filter == null || filter.isEmpty()) 
                ? userService.findAll(pageable)
                : userService.searchUsersBySimilarName(filter, pageable);
        return (filter == null || filter.isEmpty())
                ? userService.findAllExcludingUser(currentUserId, pageable)
                : userService.searchUsersBySimilarNameExcludingUser(filter, currentUserId, pageable);
    }

    public Page<ExerciseList> searchLists(Pageable pageable, String filter, Long ownerId) {
        if (ownerId == null)
            return (filter == null || filter.isEmpty())
                    ? listService.findAll(pageable)
                    : listService.searchListsBySimilarTitle(filter, pageable);
        User currentUser = userService.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
        return listService.findByOwner(currentUser, pageable);
    }

    public Page<Exercise> searchExercises(Pageable pageable, String filter, Long listId) {
        if (listId == null)
            return (filter == null || filter.isEmpty())
                    ? exerciseService.findAll(pageable)
                    : exerciseService.searchExercisesBySimilarTitle(filter, pageable);
        return exerciseService.findByExerciseListId(pageable , listId);
    }

    public Page<Post> searchPosts(Pageable pageable, String filter, Long currentUserId){
        User currentUser = (currentUserId == null)
            ? null 
            : userService.findById(currentUserId).orElseThrow(() -> new NoSuchElementException());
        Page<Post> posts = (currentUser == null)? postService.findAll(pageable) : postService.findFeedForUser(currentUser, pageable);

        for (Post p : posts){
            p.calculateTime();
        }
        return posts;
    }

}