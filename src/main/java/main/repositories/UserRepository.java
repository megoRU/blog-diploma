package main.repositories;

import main.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users (email, name, reg_time, password, is_moderator, code) VALUES (:email, :name, :time, :password, 0, :code)", nativeQuery = true)
    void insertUser(@Param("email") String email,
                    @Param("name") String name,
                    @Param("time") Date time,
                    @Param("password") String password,
                    @Param("code") String code);
}
