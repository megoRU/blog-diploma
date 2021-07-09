package main.repositories;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND  p.time < CURRENT_DATE ORDER BY p.time")
    List<Post> findAllPosts();

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_DATE ORDER BY p.time DESC")
    Page<Post> findAllPostsByTimeDesc(Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_DATE ORDER BY p.time ASC")
    Page<Post> findAllPostsByTime(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.`time` < NOW() ORDER BY (SELECT count(*) FROM post_comments c WHERE c.post_id = p.id) DESC", nativeQuery = true)
    Page<Post> findAllPostsByCommentsDesc(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND `time` < NOW() ORDER BY (SELECT count(*) FROM post_votes pv WHERE pv.post_id = p.id) DESC", nativeQuery = true)
    Page<Post> findAllPostsByVotesDesc(Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.text LIKE %:query% AND p.time < CURRENT_DATE ORDER BY p.time DESC")
    Page<Post> findAllPostsByName(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE is_active = true AND moderation_status = 'ACCEPTED' AND `time` BETWEEN :dateFirst AND :dateSecond ORDER BY time", nativeQuery = true)
    Page<Post> findAllPostsByDate(@Param("dateFirst") String dateFirst, @Param("dateSecond") String dateSecond, Pageable pageable);

    @Query(value = "SELECT p " +
            "FROM Post p " +
            "JOIN Tags2Post t2p on t2p.post.id = p.id " +
            "JOIN Tag t on t2p.tag.id = t.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND t.name = :tag")
    Page<Post> findAllPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.id = :id")
    Post findPostById(@Param("id") int id);

}
