package main.repositories;

import main.dto.responses.StatisticsResponse;
import main.model.Post;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND  p.time < CURRENT_TIME ORDER BY p.time")
    List<Post> findAllPosts();

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME ORDER BY p.time DESC")
    Page<Post> findAllPostsByTimeDesc(Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME ORDER BY p.time ASC")
    Page<Post> findAllPostsByTime(Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p, PostComment pc WHERE p.isActive = 1 " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIME ORDER BY SIZE(p.comment) DESC")
    Page<Post> findAllPostsByCommentsDesc(Pageable pageable);

    @Query(value = "SELECT new main.dto.responses.StatisticsResponse(" +
            "(SELECT COUNT(p.id) FROM Post p WHERE p.user.id = :userId AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED') AS postCount, " +
            "SUM (CASE WHEN pv.value = 1 THEN 1 ELSE 0 END)  AS countLikes, " +
            "SUM (CASE WHEN pv.value = -1 THEN 1 ELSE 0 END) AS countDislikes, " +
            "(SELECT SUM(p.viewCount) FROM Post p WHERE p.user.id = :userId AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED') AS sumView, " +
            "MIN(p.time) AS firstPublication) " +
            "FROM Post p " +
            "JOIN PostVote pv on p.id = pv.post.id " +
            "WHERE p.user.id = :userId AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' ")
    StatisticsResponse getMyPostSumViewFromPosts(@Param("userId") Integer userId);

    @Query(value = "SELECT new main.dto.responses.StatisticsResponse(" +
            "(SELECT COUNT(p.id) FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED') AS postCount, " +
            "SUM (CASE WHEN pv.value = 1 THEN 1 ELSE 0 END)  AS countLikes, " +
            "SUM (CASE WHEN pv.value = -1 THEN 1 ELSE 0 END) AS countDislikes, " +
            "(SELECT SUM(p.viewCount) FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED') AS sumView, " +
            "MIN(p.time) AS firstPublication) " +
            "FROM Post p " +
            "JOIN PostVote pv on p.id = pv.post.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' ")
    StatisticsResponse getAllPostSumViewFromPosts();

    @Modifying
    @Query(value = "SELECT DISTINCT p, " +
            "(SELECT DISTINCT COUNT(pv) AS countLikes FROM PostVote pv WHERE pv.post.id = p.id) " +
            "AS countLikes " +
            "FROM Post p " +
            "LEFT JOIN PostVote pv ON pv.post.id = p.id WHERE p.isActive = 1 " +
            "AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME ORDER BY countLikes DESC ")
    List<Post> findAllPostsByVotesDesc();

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND (p.text LIKE %:query% OR p.title LIKE %:query%) AND p.time < CURRENT_TIME ORDER BY p.time DESC")
    Page<Post> findAllPostsByName(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time BETWEEN :dateFirst AND :dateSecond ORDER BY p.time")
    Page<Post> findAllPostsByDate(@Param("dateFirst") LocalDateTime dateFirst, @Param("dateSecond") LocalDateTime dateSecond, Pageable pageable);

    @Query(value = "SELECT p " +
            "FROM Post p " +
            "JOIN Tags2Post t2p ON t2p.post.id = p.id " +
            "JOIN Tag t ON t2p.tag.id = t.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND t.name = :tag")
    Page<Post> findAllPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.id = :id AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED'")
    Post findPostById(@Param("id") int id);

    @Query(value = "SELECT p FROM Post p WHERE p.id = :id AND p.isActive = 1")
    Post findPostByIdForModerator(@Param("id") int id);

    @Query(value = "SELECT p FROM Post p WHERE p.id = :id ")
    Post findPostByIdForModeratorWithOutIsActive(@Param("id") int id);

    @Query(value = "SELECT p FROM Post p WHERE p.id = :id AND p.user.id = :userId")
    Post findPostByIdForUser(@Param("id") int id, @Param("userId") int userId);

    @Query(value = "SELECT p FROM Post p WHERE p.id = :id")
    Post findPostByIdForModer(@Param("id") int id);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 " +
            "AND p.moderationStatus = :status " +
            "AND p.moderatorId = :moderatorId " +
            "AND p.time < CURRENT_TIME ORDER BY p.time")
    Page<Post> findAllPostForModerator(@Param("status") ModerationStatus status,
                                       @Param("moderatorId") Integer moderatorId, Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 " +
            "AND p.moderationStatus = 'NEW' " +
            "AND p.moderatorId IS NULL AND p.time < CURRENT_TIME ORDER BY p.time")
    Page<Post> findAllPostForModeratorNew(Pageable pageable);


    @Query(value = "SELECT p FROM Post p WHERE p.isActive = :isActive " +
            "AND p.moderationStatus = :status " +
            "AND p.user.id = :userId " +
            "AND p.time < CURRENT_TIME ORDER BY p.time")
    Page<Post> findAllMyPosts(@Param("status") ModerationStatus status,
                              @Param("isActive") Integer isActive,
                              @Param("userId") Integer userId, Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = :isActive " +
            "AND (p.moderationStatus = :statusNew OR p.moderationStatus = :statusAccepted) " +
            "AND p.user.id = :userId " +
            "AND p.time < CURRENT_TIME ORDER BY p.time")
    Page<Post> findAllMyPosts(@Param("statusNew") ModerationStatus statusNew,
                              @Param("statusAccepted") ModerationStatus statusAccepted,
                              @Param("isActive") Integer isActive,
                              @Param("userId") Integer userId, Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = :isActive " +
            "AND p.moderationStatus = :status " +
            "AND p.user.id = :userId " +
            "AND p.time < CURRENT_TIME ORDER BY p.time")
    List<Post> findAllMyPosts(@Param("status") ModerationStatus status,
                              @Param("isActive") Integer isActive,
                              @Param("userId") Integer userId);

    @Query(value = "SELECT COUNT(p) FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'NEW'")
    int findCountAllPostsForModerator(@Param("email") String email);

}
