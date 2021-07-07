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

    @Query(value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND `time` < NOW() ORDER BY time", nativeQuery = true)
    List<Post> findAllPosts();

    @Query(value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND `time` < NOW() ORDER BY time DESC", nativeQuery = true)
    Page<Post> findAllPostsByTimeDesc(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND `time` < NOW() ORDER BY time ASC", nativeQuery = true)
    Page<Post> findAllPostsByTime(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.`time` < NOW() ORDER BY (SELECT count(*) FROM post_comments c WHERE c.post_id = p.id) DESC", nativeQuery = true)
    Page<Post> findAllPostsByCommentsDesc(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND `time` < NOW() ORDER BY (SELECT count(*) FROM post_votes pv WHERE pv.post_id = p.id) DESC", nativeQuery = true)
    Page<Post> findAllPostsByVotesDesc(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE is_active = TRUE AND moderation_status = 'ACCEPTED' AND text LIKE %:query% AND `time` < NOW() ORDER BY time DESC", nativeQuery = true)
    Page<Post> findAllPostsByName(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE is_active = true AND moderation_status = 'ACCEPTED' AND `time` BETWEEN :dateFirst AND :dateSecond ORDER BY time", nativeQuery = true)
    Page<Post> findAllPostsByDate(@Param("dateFirst") String dateFirst, @Param("dateSecond") String dateSecond, Pageable pageable);

    @Query(value = "SELECT posts.id, is_active, moderation_status, moderator_id, text, time, title, view_count, user_id " +
            "FROM posts " +
            "JOIN tag2post t2p on t2p.post_id = posts.id " +
            "JOIN tags t on t2p.tag_id = t.id " +
            "WHERE is_active = true AND moderation_status = 'ACCEPTED' AND t.name = :tag", nativeQuery = true)
    Page<Post> findAllPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE id = :id", nativeQuery = true)
    Post findPostById(@Param("id") int id);

}
