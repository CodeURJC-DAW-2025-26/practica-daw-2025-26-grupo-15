package es.codeurjc.daw.library.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ExerciseListService;
import es.codeurjc.daw.library.service.ExerciseService;
import es.codeurjc.daw.library.service.UserService;

@Service
public class AdminService {

    @Autowired private UserService userService;
    @Autowired private ExerciseListService listService;
    @Autowired private ExerciseService exerciseService;

    public Slice<User> searchUsers(int page, int size, String filter) {
        return (filter == null || filter.isEmpty())
                ? userService.findAll(page, size)
                : userService.searchUsersBySimilarName(filter, page, size);
    }

    public Slice<ExerciseList> searchLists(int page, int size, String filter) {
        return (filter == null || filter.isEmpty())
                ? listService.findAll(page, size)
                : listService.searchListsBySimilarTitle(filter, page, size);
    }

    public Slice<Exercise> searchExercises(int page, int size, String filter) {
        return (filter == null || filter.isEmpty())
                ? exerciseService.findAll(page, size)
                : exerciseService.searchExercisesBySimilarTitle(filter, page, size);
    }
}