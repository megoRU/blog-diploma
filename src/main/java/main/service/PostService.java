package main.service;

import main.dto.enums.PostErrors;
import main.dto.request.CreatePost;
import main.dto.responses.*;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.repositories.CommentsRepository;
import main.repositories.PostRepository;
import main.repositories.TagsRepository;
import main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagsRepository tagsRepository;
    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;
    private static final String dateStart = " 00:00:00";
    private static final String dateEnd = " 23:59:59";
    private static final String dateRegex = "\\d.+-\\d{2}-\\d{2}";

    @Autowired
    public PostService(PostRepository postRepository, TagsRepository tagsRepository, CommentsRepository commentsRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagsRepository = tagsRepository;
        this.commentsRepository = commentsRepository;
        this.userRepository = userRepository;
    }

    public PostsResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Post> postsPage;

        switch (mode) {
            case "popular":
                postsPage = postRepository.findAllPostsByCommentsDesc(pageable);
                break;
            case "best":
                postsPage = postRepository.findAllPostsByVotesDesc(pageable);
                break;
            case "early":
                postsPage = postRepository.findAllPostsByTime(pageable);
                break;
            default:
                postsPage = postRepository.findAllPostsByTimeDesc(pageable);
                break;
        }

        return getPosts(postsPage, postRepository.findAllPosts().size());
    }

    private PostsResponse getPosts(Page<Post> postsPages, int size) {
        List<PostResponseForList> postResponseList = new ArrayList<>();
        for (Post p : postsPages) {
            postResponseList.add(new PostResponseForList(p));
        }
        return new PostsResponse(size, postResponseList);
    }

    public PostsResponse getPostsSearch(int offset, int limit, String query) {
        if (query.trim().equals("")) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByTimeDesc(pageable);
            return getPosts(postsPage, postRepository.findAllPosts().size());
        }

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postsPage = postRepository.findAllPostsByName(query, pageable);
        List<PostResponseForList> postResponseList = new ArrayList<>();

        for (Post p : postsPage) {
            postResponseList.add(new PostResponseForList(p));
        }
        return new PostsResponse(postsPage.getNumberOfElements(), postResponseList);
    }

    public PostsResponse getPostsByDate(int offset, int limit, String date) {
        if (date.matches(dateRegex)) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByDate(date + dateStart, date + dateEnd, pageable);
            List<PostResponseForList> postResponseList = new ArrayList<>();

            for (Post p : postsPage) {
                postResponseList.add(new PostResponseForList(p));
            }
            return new PostsResponse(postsPage.getNumberOfElements(), postResponseList);
        }
        return new PostsResponse(0, new ArrayList<>());
    }

    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        if (!tag.equals("")) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByTag(tag, pageable);
            List<PostResponseForList> postResponseList = new ArrayList<>();

            for (Post p : postsPage) {
                postResponseList.add(new PostResponseForList(p));
            }
            return new PostsResponse(postsPage.getNumberOfElements(), postResponseList);
        }
        return new PostsResponse(0, new ArrayList<>());
    }

    //TODO: Переписать когда будет Spring Security
    //TODO: Сделать проверку на NEW и active. Любой может открыть пост по прямой ссылке
    public ResponseEntity<?> getPostsById(Integer id) {
        Post post = postRepository.findPostById(id);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<PostComment> commentsList = commentsRepository.findComments(id);
        List<String> tagList = tagsRepository.getTagsByPost(id);
        List<PostCommentsResponse> commentsResponseList = new ArrayList<>();
        for (PostComment c : commentsList) {
            commentsResponseList.add(new PostCommentsResponse(c));
        }
        return new ResponseEntity<>(new PostResponseForList(post, commentsResponseList, tagList), HttpStatus.OK);
    }

    //TODO: Переписать когда будет Spring Security
    public ResponseEntity<?> getPostForModeration(int offset, int limit, String status) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postsPage = postRepository.findAllPostForModerator(ModerationStatus.valueOf(status), 2, pageable);

        List<PostResponseForList> postResponseList = new ArrayList<>();

        for (Post p : postsPage) {
            postResponseList.add(new PostResponseForList(p));
        }

        return new ResponseEntity<>(new PostsResponse(postsPage.getNumberOfElements(), postResponseList), HttpStatus.OK);
    }

    //TODO: Переписать когда будет Spring Security
    public ResponseEntity<?> getMyPosts(int offset, int limit, String status) {
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Post> postsPage;

        switch (status) {
            case "INACTIVE":
                postsPage = postRepository.findAllMyPosts(ModerationStatus.NEW, 0, 1, pageable);
                break;
            case "PENDING":
                postsPage = postRepository.findAllMyPosts(ModerationStatus.NEW, 1, 1, pageable);
                break;
            case "DECLINED":
                postsPage = postRepository.findAllMyPosts(ModerationStatus.DECLINED, 1, 1, pageable);
                break;
            default:
                postsPage = postRepository.findAllMyPosts(ModerationStatus.ACCEPTED, 1, 1, pageable);
        }

        List<PostResponseForList> postResponseList = new ArrayList<>();

        for (Post p : postsPage) {
            postResponseList.add(new PostResponseForList(p));
        }

        return new ResponseEntity<>(new PostsResponse(postsPage.getNumberOfElements(), postResponseList), HttpStatus.OK);
    }

    //TODO: Переписать когда будет Spring Security
    public ResponseEntity<?> createPost(CreatePost createPost) {
        Map<PostErrors, String> list = new HashMap<>();

        if (createPost.getText().length() < 3 || createPost.getText().length() > 50) {
            list.put(PostErrors.TEXT, PostErrors.TEXT.getErrors());
        }

        if (createPost.getTitle().length() < 3 || createPost.getTitle().length() > 50) {
            list.put(PostErrors.TITLE, PostErrors.TITLE.getErrors());
        }

        if (list.isEmpty()) {
            Post post = new Post();
            User user = userRepository.findByEmail("user@gmail.com").get();
            post.setUser(user);
            post.setIsActive(createPost.getActive());
            //Rewrite to UTC
            post.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(createPost.getTimestamp()), TimeZone.getDefault().toZoneId()));
            post.setTitle(createPost.getTitle());
            post.setText(createPost.getText());
            post.setModerationStatus(ModerationStatus.NEW);
            post.setModeratorId(null);
            postRepository.save(post);
            return new ResponseEntity<>(new CreatePostResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CreatePostResponse(false, list), HttpStatus.OK);
    }
}