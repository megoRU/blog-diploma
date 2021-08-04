package main.repositories;

import main.model.Tags2Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface Tags2PostRepository extends CrudRepository<Tags2Post, Integer> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Tags2Post t WHERE t.id = :id")
    void deleteTags2PostById(@Param("id") int id);

    @Query(value = "SELECT t FROM Tags2Post t WHERE t.post.id = :postId")
    List<Tags2Post> getTags2Posts(@Param("postId") int postId);

}