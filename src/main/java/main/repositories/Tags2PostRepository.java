package main.repositories;

import main.model.Tags2Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tags2PostRepository extends CrudRepository<Tags2Post, Integer> {
}
