package main.repositories;

import main.model.PostComment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends CrudRepository<PostComment, Long>  {

    @Query(value = "SELECT * FROM post_comments WHERE post_id = :id", nativeQuery = true)
    List<PostComment> findComments(@Param("id") int id);
}
