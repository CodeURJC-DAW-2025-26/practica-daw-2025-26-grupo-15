package es.codeurjc.daw.library.service.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    private Long currentAdminId;

    @FunctionalInterface
    private interface AdminSearchHandler {
        Page<?> handle(int page, int size, String inputFilter);
    }

    private final Map<String, AdminSearchHandler> handlers = Map.of(
        "au", this::searchUsers,
        "al", this::searchLists,
        "ae", this::searchExercises
    );;


    public Page<?> searchByPetition(String petition, int page, int size, String filter){
        if (!this.handlers.containsKey(petition)) throw new IllegalArgumentException("Invalid search option");
        return this.handlers.get(petition).handle(page, size, filter);
    }

    private Page<User> searchUsers(int page, int size, String filter) {
        return (filter == null || filter.isEmpty())
                ? userService.findAllExcludingUser(currentAdminId, page, size)
                : userService.searchUsersBySimilarNameExcludingUser(filter, currentAdminId, page, size);
    }

    private Page<ExerciseList> searchLists(int page, int size, String filter) {
        return (filter == null || filter.isEmpty())
                ? listService.findAll(page, size)
                : listService.searchListsBySimilarTitle(filter, page, size);
    }

    private Page<Exercise> searchExercises(int page, int size, String filter) {
        return (filter == null || filter.isEmpty())
                ? exerciseService.findAll(page, size)
                : exerciseService.searchExercisesBySimilarTitle(filter, page, size);
    }

    public void setCurrentAdminId(Long id){
        this.currentAdminId = id;
    }
}