package main.model.repositories;

import main.model.GlobalSettings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Long> {

    @Query(value = "SELECT * FROM global_settings WHERE code = :code", nativeQuery = true)
    GlobalSettings findAllGlobalSettings(@Param("code") String code);
}
