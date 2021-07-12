package main.service;

import main.dto.responses.LoginResponse;
import main.model.User;
import main.repositories.PostRepository;
import main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class CheckService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public CheckService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public ResponseEntity<?> getCheck(Principal principal) {
        Optional<User> user = userRepository.findByEmail(principal.getName());
        return new ResponseEntity<>(new LoginResponse(
                true,
                user.get(),
                postRepository.findCountAllPostsForModerator(principal.getName())),
                HttpStatus.OK);
    }

}
