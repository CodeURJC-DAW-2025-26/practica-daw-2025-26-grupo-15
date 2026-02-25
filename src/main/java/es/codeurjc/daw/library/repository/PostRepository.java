package es.codeurjc.daw.library.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.daw.library.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
    SELECT p
    FROM PostTable p
    WHERE p.owner IN (
        SELECT f FROM UserTable u JOIN u.following f WHERE u.id = :userId
    )
    ORDER BY p.date DESC
    """)
    Slice<Post> findFeedForUser(@Param("userId") Long userId, Pageable pageable);
}

