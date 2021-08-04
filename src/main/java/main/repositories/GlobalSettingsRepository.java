package main.repositories;

import main.model.GlobalSettings;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE GlobalSettings s SET s.value = :value WHERE s.code = :code")
    void insertSettings(@Param("code") String code, @Param("value") String value);

    @Query(value = "FROM GlobalSettings s WHERE s.code = :code")
    GlobalSettings getSettingsById(@Param("code") String code);

}
