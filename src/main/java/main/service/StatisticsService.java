package main.service;

import lombok.AllArgsConstructor;
import main.dto.responses.StatisticsResponse;
import main.model.Post;
import main.repositories.PostRepository;
import main.security.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    private final UserService userService;

    public ResponseEntity<StatisticsResponse> getMyStatistics() {

//        StatisticsResponse statisticsResponse = new StatisticsResponse(postRepository.getMyPostSumViewFromPosts(userService.getCurrentUser().getId()));
//
//        System.out.println(postRepository.getMyPostSumViewFromPosts(userService.getCurrentUser().getId()).size());

//        return null;
        return new ResponseEntity<>(postRepository.getMyPostSumViewFromPosts(userService.getCurrentUser().getId()), HttpStatus.OK);
    }

    public ResponseEntity<?> getAllStatistics() {

        Post allStatistics = postRepository.findAllPostForGlobalStatistic();
        Post sumView = postRepository.getPostSumViewFromPosts();

        return new ResponseEntity<>(new StatisticsResponse(
                sumView.getPostCount(),
                allStatistics.getCountLikes(),
                allStatistics.getCountDislikes(),
                sumView.getSumView(),
                allStatistics.getFirstPost().toEpochSecond(ZoneOffset.UTC)), HttpStatus.OK);

    }
}