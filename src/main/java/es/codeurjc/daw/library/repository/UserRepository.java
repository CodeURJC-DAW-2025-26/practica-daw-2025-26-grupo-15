package es.codeurjc.daw.library.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import java.util.List;

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

    @Query(value = """
    SELECT *
    FROM user_table u
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) AND u.id <> :excludedUserId
    ORDER BY 
        CASE 
            WHEN LOWER(u.name) LIKE LOWER(CONCAT(:name, '%')) THEN 0
            WHEN LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) THEN 1
            ELSE 2
        END,
        LENGTH(u.name),
        u.name ASC
    """, nativeQuery = true)
    Slice<User> searchUsersBySimilarNameExcludingUser(@Param("name") String name, @Param("excludedUserId") Long excludedUserId, Pageable pageable);

    @Query(value = """
    SELECT *
    FROM user_table u
    WHERE u.id <> :excludedUserId
    """, nativeQuery = true)
    Slice<User> findAllExcludingUser(@Param("excludedUserId") Long excludedUserId, Pageable pageable);


    //suggestion based on random users that are not friends or pending friends
    @Query(value = "SELECT * FROM user_table u " +
                   "WHERE u.id != :userId " + //not suggest myself
                   "AND u.id NOT IN (SELECT rf.requested_friends_id FROM user_table_requested_friends rf WHERE rf.request_received_id = :userId) " + //not suggest pending friends
                   "ORDER BY RAND()", nativeQuery = true)
    Page<User> findRandomUsers(Long userId, Pageable pageable);

    // suggestion based on friends of friends
    @Query(value = "SELECT u.* FROM user_table u " +
                   "JOIN user_table_following f2 ON u.id = f2.following_id " + //Friend of friend
                   "WHERE f2.followers_id IN (SELECT f1.following_id FROM user_table_following f1 WHERE f1.followers_id = :userId) " + //f1: my friends
                   "AND u.id != :userId " + 
                   "AND u.id NOT IN (SELECT following_id FROM user_table_following WHERE followers_id = :userId) " + //no suggestions of my friends
                   "AND u.id NOT IN (SELECT requested_friends_id FROM user_table_requested_friends WHERE request_received_id = :userId) " + //no suggestion if friend request
                   "GROUP BY u.id " +
                   "ORDER BY COUNT(f2.followers_id) DESC", nativeQuery = true) //order by number of mutual friends
    List<User> findFollowingSuggestions(Long userId);

}
