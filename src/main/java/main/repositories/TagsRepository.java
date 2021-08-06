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

    @Query(value = "SELECT t.name AS name, COUNT(t.name) AS count " +
            "FROM Tag t " +
            "JOIN Tags2Post tp ON tp.tag.id = t.id " +
            "JOIN Post p ON p.id = tp.post.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME AND t.name LIKE %:name% " +
            "GROUP BY t.name ORDER BY COUNT(t.name) DESC")
    List<TagsResponseImpl> getTagByName(@Param("name") String name);

    @Query(value = "SELECT t FROM Tag t WHERE t.name = :name")
    Tag getTagIdByName(@Param("name") String name);

    @Query(value = "SELECT t.name AS name, COUNT(t.name) AS count " +
            "FROM Tag t " +
            "JOIN Tags2Post tp ON tp.tag.id = t.id " +
            "JOIN Post p ON p.id = tp.post.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME GROUP BY t.name ORDER BY COUNT(t.name) DESC")
    List<TagsResponseImpl> getRecentTags();

    @Query(value = "SELECT t.name " +
            "FROM Tag t " +
            "JOIN Tags2Post tp ON tp.tag.id = t.id " +
            "WHERE tp.post.id = :postId")
    List<String> getTagsByPostString(@Param("postId") Integer postId);

}