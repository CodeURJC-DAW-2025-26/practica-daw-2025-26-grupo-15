package es.codeurjc.daw.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.daw.library.model.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    

}