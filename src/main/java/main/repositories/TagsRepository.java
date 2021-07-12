package main.repositories;

import main.dto.responses.TagsResponseImpl;
import main.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagsRepository extends CrudRepository<Tag, Integer> {

    @Query(value = "SELECT t.name as name, COUNT(t.id) as count " +
    "FROM Tag t " +
    "JOIN Tags2Post tp ON tp.tag.id = t.id " +
    "JOIN Post p on p.id = tp.post.id " +
    "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_DATE AND t.name = (:name) GROUP BY tp.tag.id ORDER BY count(t.id) DESC")
    List<TagsResponseImpl> getTagByName(@Param("name") String name);


    @Query(value = "SELECT t.name as name, COUNT(t.id) as count " +
            "FROM Tag t " +
            "JOIN Tags2Post tp ON tp.tag.id = t.id " +
            "JOIN Post p on p.id = tp.post.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_DATE GROUP BY tp.tag.id ORDER BY count(t.id) DESC")
    List<TagsResponseImpl> getRecentTags();

    @Query(value = "SELECT t.name " +
            "FROM Tag t " +
            "JOIN Tags2Post tp ON tp.tag.id = t.id " +
            "WHERE tp.post.id = :id")
    List<String> getTagsByPost(@Param("id") Integer id);

}
