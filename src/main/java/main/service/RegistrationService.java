package main.service;

import main.dto.enums.RegistrationErrors;
import main.dto.request.RegistrationRequest;
import main.dto.responses.RegistrationResponse;
import main.repositories.CaptchaRepository;
import main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class RegistrationService {

    private final CaptchaRepository captchaRepository;
    private final UserRepository userRepository;

    @Autowired
    public RegistrationService(CaptchaRepository captchaRepository, UserRepository userRepository) {
        this.captchaRepository = captchaRepository;
        this.userRepository = userRepository;
    }

    public RegistrationResponse registration(@RequestBody RegistrationRequest registrationRequest) {
        Map<RegistrationErrors, String> list = new HashMap<>();
        System.out.println(registrationRequest.toString());

        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            list.put(RegistrationErrors.EMAIL, RegistrationErrors.EMAIL.getErrors());
        }

        if (!registrationRequest.getName().matches("[A-Za-zА-Яа-я0-9]+")) {
            list.put(RegistrationErrors.NAME, RegistrationErrors.NAME.getErrors());
        }

        if (registrationRequest.getPassword().length() < 6) {
            list.put(RegistrationErrors.PASSWORD, RegistrationErrors.PASSWORD.getErrors());
        }

        if (!registrationRequest.getCaptcha().equals(captchaRepository.checkCaptcha(registrationRequest.getCaptcha_secret()))) {
            list.put(RegistrationErrors.CAPTCHA, RegistrationErrors.CAPTCHA.getErrors());
        }

        if (list.isEmpty()) {
            userRepository.insertUser(
                    registrationRequest.getEmail(),
                    registrationRequest.getName(),
                    new Date(),
                    new BCryptPasswordEncoder(12).encode(registrationRequest.getPassword()),
                    getRandomNumber());
            return new RegistrationResponse(true);
        }

        return new RegistrationResponse(false, list);
    }

    private String getRandomNumber() {
        return String.format("%06d", new Random().nextInt(999999));
    }

}
