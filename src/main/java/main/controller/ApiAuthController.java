package main.controller;

import main.api.request.LoginRequest;
import main.api.responses.LoginResponse;
import main.api.responses.UserLoginResponseList;
import main.model.User;
import main.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@RestController
public class ApiAuthController {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CheckService checkService;

    @Autowired
    public ApiAuthController(UserRepository userRepository, CheckService checkService) {
        this.userRepository = userRepository;
        this.checkService = checkService;
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

}
