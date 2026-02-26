package es.codeurjc.daw.library.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;

public interface ExerciseListRepository extends JpaRepository<ExerciseList, Long> {
    List<ExerciseList> findByOwner(User owner);

    Slice<ExerciseList> findByOwner(User owner, Pageable pageable);
}
