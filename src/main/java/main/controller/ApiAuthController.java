package main.controller;

import main.api.request.LoginRequest;
import main.api.responses.InitResponse;
import main.api.responses.LoginResponse;
import main.api.responses.UserLoginResponse;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    //Доделать
    private final InitResponse initResponse;

    @Autowired
    public ApiAuthController(AuthenticationManager authenticationManager, UserRepository userRepository, InitResponse initResponse) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.initResponse = initResponse;
    }

    @GetMapping("/api/auth/check")
    private InitResponse check() {
        return initResponse;
    }

    @PostMapping("/api/auth/login")
    private ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = (User) auth.getPrincipal();

        main.model.User currentUser = userRepository
                .findByEmail(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setEmail(currentUser.getEmail());
        userLoginResponse.setModeration(currentUser.getIsModerator() == 1);
        userLoginResponse.setId(currentUser.getId());


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userLoginResponse);

        System.out.println(loginRequest.getEmail());
        return ResponseEntity.ok(loginResponse);
    }

}
