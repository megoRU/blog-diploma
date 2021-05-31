package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.LoginRequest;
import main.api.responses.InitResponse;
import main.api.responses.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ApiAuthController {

    //Доделать
    private final InitResponse initResponse;

    @GetMapping("/api/auth/check")
    private InitResponse check() {
        return initResponse;
    }

    @PostMapping("/api/auth/login")
    private ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest.getEmail());
        return ResponseEntity.ok(new LoginResponse());
    }

}
