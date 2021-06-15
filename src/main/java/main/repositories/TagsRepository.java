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

//    List<TagsResponseImpl> findAll();

    @Query(nativeQuery = true, value = "SELECT t.name as name, COUNT(t.id) as count " +
    "FROM tags t " +
    "JOIN tag2post tp ON tp.tag_id = t.id " +
    "JOIN posts p on p.id = tp.post_id " +
    "WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.time < NOW() AND t.name = (:name) GROUP BY tp.tag_id ORDER BY count DESC")
    List<TagsResponseImpl> getTagByName(@Param("name") String name);


    @Query(nativeQuery = true, value = "SELECT t.name as name, COUNT(t.id) as count " +
            "FROM tags t " +
            "JOIN tag2post tp ON tp.tag_id = t.id " +
            "JOIN posts p on p.id = tp.post_id " +
            "WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.time < NOW() GROUP BY tp.tag_id ORDER BY count DESC")
    List<TagsResponseImpl> getRecentTags();

}
