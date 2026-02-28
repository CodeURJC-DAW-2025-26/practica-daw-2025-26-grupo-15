package es.codeurjc.daw.library.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.daw.library.model.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query(value = """
    SELECT *
    FROM exercise_table e
    WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))
    ORDER BY 
        CASE 
            WHEN LOWER(e.title) LIKE LOWER(CONCAT(:title, '%')) THEN 0
            WHEN LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')) THEN 1
            ELSE 2
        END,
        LENGTH(e.title),
        e.title ASC
    """, nativeQuery = true)
    Slice<Exercise> searchExercisesBySimilarTitle(@Param("title") String title, Pageable pageable);
}