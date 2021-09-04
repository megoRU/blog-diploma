package main.service;

import lombok.AllArgsConstructor;
import main.dto.responses.StatisticsResponse;
import main.repositories.PostRepository;
import main.security.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    private final UserService userService;

    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        return new ResponseEntity<>(postRepository.getMyPostSumViewFromPosts(userService.getCurrentUser().getId()), HttpStatus.OK);
    }

    public ResponseEntity<StatisticsResponse> getAllStatistics() {
        return new ResponseEntity<>(postRepository.getAllPostSumViewFromPosts(), HttpStatus.OK);
    }
}