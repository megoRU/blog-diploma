package main.service;

import lombok.RequiredArgsConstructor;
import main.dto.responses.LoginResponse;
import main.dto.responses.ResultResponse;
import main.repositories.PostRepository;
import main.repositories.UserRepository;
import main.security.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CheckService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    //TODO: Проверка на NULL User
    public ResponseEntity<?> getCheck(Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }

        return new ResponseEntity<>(new LoginResponse(
                true,
                userService.getCurrentUser(),
                postRepository.findCountAllPostsForModerator(principal.getName())),
                HttpStatus.OK);
    }

}
