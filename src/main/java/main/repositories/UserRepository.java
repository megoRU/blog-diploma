package main.repositories;

import main.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    User findByEmailForProfile(String email);

    @Query(value = "SELECT u FROM User u WHERE u.code = :code")
    User findUserCode(@Param("code") String code);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.password = :password, u.code = NULL, u.hashTime = NULL WHERE u.id = :userId")
    void updatePasswordAndDeleteCode(@Param("password") String password, @Param("userId") int userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email, u.password = :password, u.photo = :photo WHERE u.id = :id")
    void updatePasswordPhoto(@Param("name") String name,
                             @Param("email") String email,
                             @Param("password") String password,
                             @Param("photo") String photo,
                             @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email, u.photo = :photo WHERE u.id = :id")
    void updateNameEmailPhoto(@Param("name") String name,
                              @Param("email") String email,
                              @Param("photo") String photo,
                              @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email WHERE u.id = :id")
    void updateNameAndEmail(
            @Param("name") String name,
            @Param("email") String email,
            @Param("id") int id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email, u.password = :password WHERE u.id = :id")
    void updateNameEmailPassword(@Param("name") String name,
                                 @Param("email") String email,
                                 @Param("password") String password,
                                 @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.code = :code, u.hashTime = :time WHERE u.id = :id")
    void updateUserCode(@Param("code") String code, @Param("id") int id, @Param("time") LocalDateTime time);
}
