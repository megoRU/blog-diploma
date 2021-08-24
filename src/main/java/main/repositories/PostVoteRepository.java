package main.repositories;

import main.model.PostVote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {

    @Query(value = "SELECT pv FROM PostVote pv WHERE pv.post.id = :post_id AND pv.user.id = :user_id")
    PostVote getPostVoteByIdAndByUserId(@Param("post_id") Integer postId, @Param("user_id") Integer userId);
}
