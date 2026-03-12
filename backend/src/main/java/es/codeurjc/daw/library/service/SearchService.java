package es.codeurjc.daw.library.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    

    
    public Page<User> searchUsers(int page, int size, String filter, Long currentUserId) {
        if (currentUserId == null) 
            return (filter == null || filter.isEmpty()) 
                ? userService.findAll(page, size)
                : userService.searchUsersBySimilarName(filter, page, size);
        return (filter == null || filter.isEmpty())
                ? userService.findAllExcludingUser(currentUserId, page, size)
                : userService.searchUsersBySimilarNameExcludingUser(filter, currentUserId, page, size);
    }

    public Page<ExerciseList> searchLists(int page, int size, String filter, Long ownerId) {
        if (ownerId == null)
            return (filter == null || filter.isEmpty())
                    ? listService.findAll(page, size)
                    : listService.searchListsBySimilarTitle(filter, page, size);
        User currentUser = userService.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
        return listService.findByOwner(currentUser, page, size);
    }

    public Page<Exercise> searchExercises(int page, int size, String filter, Long listId) {
        if (listId == null)
            return (filter == null || filter.isEmpty())
                    ? exerciseService.findAll(page, size)
                    : exerciseService.searchExercisesBySimilarTitle(filter, page, size);
        return exerciseService.findByListId(page, size, listId);
    }

    public Page<Post> searchPosts(int page, int size, String filter, Long currentUserId){
        User currentUser = userService.findById(currentUserId).orElse(null);
        Page<Post> posts = (currentUser == null)? postService.findAll(page, size) : postService.findFeedForUser(currentUser, page, size);

        for (Post p : posts){
            p.calculateTime();
        }
        return posts;
    }

}