package main.repositories;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaRepository extends CrudRepository<CaptchaCode, Long> {

    @Query(value = "SELECT c.code FROM CaptchaCode c WHERE c.secretCode = :secret_captcha")
    String checkCaptcha(@Param("secret_captcha") String secret_captcha);
}
