package main.controller;

import lombok.RequiredArgsConstructor;
import main.dto.request.LoginRequest;
import main.dto.request.RegistrationRequest;
import main.dto.responses.ResultResponse;
import main.service.CaptchaService;
import main.service.CheckService;
import main.service.LoginService;
import main.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ApiAuthController {

    private final LoginService loginService;
    private final CheckService checkService;
    private final CaptchaService captchaService;
    private final RegistrationService registrationService;

    @GetMapping("/api/auth/check")
    private ResponseEntity<?> check(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
        return checkService.getCheck(principal);
    }

    @PostMapping("/api/auth/login")
    private ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @GetMapping(value = "/api/auth/captcha")
    private ResponseEntity<?> captcha() throws IOException {
        return captchaService.getCaptcha();
    }

    @PostMapping(value = "/api/auth/register")
    private ResponseEntity<?> registration(@RequestBody RegistrationRequest registrationRequest) {
        return registrationService.registration(registrationRequest);
    }

}
