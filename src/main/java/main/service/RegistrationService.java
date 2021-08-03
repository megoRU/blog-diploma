package main.service;

import lombok.RequiredArgsConstructor;
import main.dto.enums.RegistrationErrors;
import main.dto.request.RegistrationRequest;
import main.dto.responses.CreateResponse;
import main.dto.responses.ResultResponse;
import main.model.User;
import main.repositories.CaptchaRepository;
import main.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final CaptchaRepository captchaRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> registration(@RequestBody RegistrationRequest registrationRequest) {
        Map<RegistrationErrors, String> list = new HashMap<>();

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
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setName(registrationRequest.getName());
            user.setRegTime(LocalDateTime.now());
            user.setPassword(new BCryptPasswordEncoder(12).encode(registrationRequest.getPassword()));
            userRepository.save(user);
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
    }

}
