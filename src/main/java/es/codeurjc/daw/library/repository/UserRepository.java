package es.codeurjc.daw.library.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.daw.library.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    @Query(value = """
    SELECT *
    FROM user_table u
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))
    ORDER BY 
        CASE 
            WHEN LOWER(u.name) LIKE LOWER(CONCAT(:name, '%')) THEN 0
            WHEN LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) THEN 1
            ELSE 2
        END,
        LENGTH(u.name),
        u.name ASC
    """, nativeQuery = true)
    Slice<User> searchUsersBySimilarName(@Param("name") String name, Pageable pageable);

}
