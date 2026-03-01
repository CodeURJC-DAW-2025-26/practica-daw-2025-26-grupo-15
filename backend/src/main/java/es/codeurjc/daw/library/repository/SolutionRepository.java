package es.codeurjc.daw.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.daw.library.model.Solution;


public interface SolutionRepository extends JpaRepository<Solution, Long>{
}
