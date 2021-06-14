package main.repositories;

import main.model.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Posts, Integer> {

    @Query(value = "SELECT p.* FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.`time` < NOW() ORDER BY p.time", nativeQuery = true)
    List<Posts> findAllPosts();

    @Query(value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND `time` < NOW() ORDER BY time DESC", nativeQuery = true)
    Page<Posts> findAllPostsByTimeDesc(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND `time` < NOW() ORDER BY time ASC", nativeQuery = true)
    Page<Posts> findAllPostsByTime(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.`time` < NOW() ORDER BY (SELECT count(*) FROM post_comments c WHERE c.post_id = p.id) DESC", nativeQuery = true)
    Page<Posts> findAllPostsByCommentsDesc(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND `time` < NOW() ORDER BY (SELECT count(*) FROM post_votes pv WHERE pv.post_id = p.id) DESC", nativeQuery = true)
    Page<Posts> findAllPostsByVotesDesc(Pageable pageable);
}
