package main.service;

import lombok.RequiredArgsConstructor;
import main.dto.request.LoginRequest;
import main.dto.responses.LoginResponse;
import main.dto.responses.ResultResponse;
import main.model.User;
import main.repositories.PostRepository;
import main.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        Optional<User> findUser = userRepository.findByEmail(loginRequest.getEmail());

        if (findUser.isEmpty()) {
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
        User user = findUser.get();
        if (new BCryptPasswordEncoder(12).matches(loginRequest.getPassword(), user.getPassword())) {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(auth);

            return new ResponseEntity<>(new LoginResponse(
                    true,
                    user,
                    postRepository.findCountAllPostsForModerator(user.getEmail())),
                    HttpStatus.OK);
        }
        return ResponseEntity.ok(new ResultResponse(false));
    }
}
