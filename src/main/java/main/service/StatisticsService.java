package main.service;

import lombok.AllArgsConstructor;
import main.dto.responses.StatisticsResponse;
import main.model.Post;
import main.model.PostVote;
import main.model.enums.ModerationStatus;
import main.repositories.GlobalSettingsRepository;
import main.repositories.PostRepository;
import main.security.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final GlobalSettingsRepository globalSettingsRepository;

    public ResponseEntity<?> getMyStatistics() {
        List<Post> postsList = postRepository.findAllMyPosts(ModerationStatus.ACCEPTED, 1, userService.getCurrentUser().getId());

        if (postsList.isEmpty()) {
            return new ResponseEntity<>(new StatisticsResponse(), HttpStatus.OK);
        }

        long postsCount = postsList.size();
        long likeCount = 0;
        long disLikeCount = 0;
        long viewsCount = 0;
        long firstPublication = postsList.get(0).getTime().toEpochSecond(ZoneOffset.UTC);

        for (int i = 0; i < postsList.size(); i++) {
            likeCount += getLikeCount(postsList.get(i));
            disLikeCount += getDislikeCount(postsList.get(i));
            viewsCount += postsList.get(i).getViewCount();
        }


        return new ResponseEntity<>(new StatisticsResponse(
                postsCount,
                likeCount,
                disLikeCount,
                viewsCount,
                firstPublication), HttpStatus.OK);
    }

    public ResponseEntity<?> getAllStatistics(Principal principal) {
        boolean isModerator = false;
        if (principal != null) {
            isModerator = userService.getCurrentUser().getIsModerator() == 1;
        }

        if (globalSettingsRepository.getSettingsById("STATISTICS_IS_PUBLIC").getValue().equals("NO") && !isModerator) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Post> postsList = postRepository.findAllPosts();

        if (postsList.isEmpty()) {
            return new ResponseEntity<>(new StatisticsResponse(), HttpStatus.OK);
        }

        long postsCount = postsList.size();
        long likeCount = 0;
        long disLikeCount = 0;
        long viewsCount = 0;
        long firstPublication = postsList.get(0).getTime().toEpochSecond(ZoneOffset.UTC);


        for (int i = 0; i < postsList.size(); i++) {
            likeCount += getLikeCount(postsList.get(i));
            disLikeCount += getDislikeCount(postsList.get(i));
            viewsCount += postsList.get(i).getViewCount();
        }

        return new ResponseEntity<>(new StatisticsResponse(
                postsCount,
                likeCount,
                disLikeCount,
                viewsCount,
                firstPublication), HttpStatus.OK);
    }

    private long getLikeCount(Post post) {
        long likeCount = 0;

        if (post.getLike() != null) {
            ArrayList<PostVote> likes = new ArrayList<>(post.getLike());
            for (PostVote like : likes) {
                if (like.getValue() == 1) {
                    likeCount++;
                }
            }
        }
        return likeCount;
    }

    private long getDislikeCount(Post post) {
        long dislikeCount = 0;

        if (post.getLike() != null) {
            ArrayList<PostVote> dislikes = new ArrayList<>(post.getLike());
            for (PostVote dis : dislikes) {
                if (dis.getValue() == -1) {
                    dislikeCount++;
                }
            }
        }
        return dislikeCount;
    }

}