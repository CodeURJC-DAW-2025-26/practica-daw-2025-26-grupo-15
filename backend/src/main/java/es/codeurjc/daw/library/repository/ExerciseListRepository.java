package es.codeurjc.daw.library.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.User;

public interface ExerciseListRepository extends JpaRepository<ExerciseList, Long> {
    List<ExerciseList> findByOwner(User owner);

    Slice<ExerciseList> findByOwner(User owner, Pageable pageable);

    @Query(value = """
    SELECT *
    FROM exercise_list_table l
    WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :title, '%'))
    ORDER BY 
        CASE 
            WHEN LOWER(l.title) LIKE LOWER(CONCAT(:title, '%')) THEN 0
            WHEN LOWER(l.title) LIKE LOWER(CONCAT('%', :title, '%')) THEN 1
            ELSE 2
        END,
        LENGTH(l.title),
        l.title ASC
    """, nativeQuery = true)
    Slice<ExerciseList> searchListsBySimilarTitle(@Param("title") String title, Pageable pageable);
}
