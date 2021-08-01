package main.repositories;

import main.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email, u.password = :password, u.photo = :photo WHERE u.id = :id")
    void editPasswordPhoto(@Param("name") String name,
                           @Param("email") String email,
                           @Param("password") String password,
                           @Param("photo") String photo,
                           @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email, u.photo = :photo WHERE u.id = :id")
    void editNameEmailPhoto(@Param("name") String name,
                           @Param("email") String email,
                           @Param("photo") String photo,
                           @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email, u.photo = :photo WHERE u.id = :id")
    void editPhoto(@Param("name") String name,
                   @Param("email") String email,
                   @Param("photo") String photo,
                   @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email WHERE u.id = :id")
    void editNameAndEmail(
            @Param("name") String name,
            @Param("email") String email,
            @Param("id") int id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.name = :name, u.email = :email, u.password = :password WHERE u.id = :id")
    void editNameEmailPassword(@Param("name") String name,
                               @Param("email") String email,
                               @Param("password") String password,
                               @Param("id") int id);

}
