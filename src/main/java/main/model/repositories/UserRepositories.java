package main.model.repositories;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepositories extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
