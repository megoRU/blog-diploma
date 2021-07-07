package main.controller;

import main.dto.request.LoginRequest;
import main.dto.request.RegistrationRequest;
import main.dto.responses.LoginResponse;
import main.dto.responses.RegistrationResponse;
import main.dto.responses.UserLoginResponseList;
import main.model.User;
import main.repositories.UserRepository;
import main.service.CaptchaService;
import main.service.CheckService;
import main.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
public class ApiAuthController {

    private final UserRepository userRepository;
    private final CheckService checkService;
    private final CaptchaService captchaService;
    private final RegistrationService registrationService;


    @Autowired
    public ApiAuthController(UserRepository userRepository, CheckService checkService, CaptchaService captchaService, RegistrationService registrationService) {
        this.userRepository = userRepository;
        this.checkService = checkService;
        this.captchaService = captchaService;
        this.registrationService = registrationService;
    }

    @GetMapping("/api/auth/check")
    private ResponseEntity<UserLoginResponseList> check() {
//        System.out.println(checkService.getCheck());
//        if (checkService.getCheck() == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }

        return ResponseEntity.ok(checkService.getCheck());
    }

    @PostMapping(value = "/api/auth/login")
    private ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        System.out.println(loginRequest.getEmail());

        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        System.out.println(user.get().getPassword());

        System.out.println(userRepository.findByEmail(loginRequest.getEmail()));

        return ResponseEntity.ok(new LoginResponse());
    }

    @GetMapping(value = "/api/auth/captcha")
    private ResponseEntity<?> captcha() throws IOException {
        return captchaService.getCaptcha();
    }

    @PostMapping(value = "/api/auth/register")
    private ResponseEntity<RegistrationResponse> registration(@RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(registrationService.registration(registrationRequest));
    }

}
