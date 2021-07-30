package main.service;

import lombok.RequiredArgsConstructor;
import main.dto.enums.PostErrors;
import main.dto.request.CreatePost;
import main.dto.responses.*;
import main.model.*;
import main.model.enums.ModerationStatus;
import main.repositories.*;
import main.security.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagsRepository tagsRepository;
    private final Tags2PostRepository tags2PostRepository;
    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final GlobalSettingsRepository globalSettingsRepository;
    private static final String dateStart = " 00:00:00";
    private static final String dateEnd = " 23:59:59";
    private static final String dateRegex = "\\d.+-\\d{2}-\\d{2}";

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
        List<PostResponseForList> postResponseList = postsPages.get().map(PostResponseForList::new).collect(Collectors.toList());
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

        List<PostResponseForList> postResponseList = postsPage.get().map(PostResponseForList::new).collect(Collectors.toList());

        return new PostsResponse(postsPage.getNumberOfElements(), postResponseList);
    }

    public PostsResponse getPostsByDate(int offset, int limit, String date) {
        if (date.matches(dateRegex)) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByDate(date + dateStart, date + dateEnd, pageable);

            List<PostResponseForList> postResponseList = postsPage.get().map(PostResponseForList::new).collect(Collectors.toList());

            return new PostsResponse(postsPage.getNumberOfElements(), postResponseList);
        }
        return new PostsResponse(0, new ArrayList<>());
    }

    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        if (!tag.equals("")) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByTag(tag, pageable);

            List<PostResponseForList> postResponseList = postsPage.get().map(PostResponseForList::new).collect(Collectors.toList());

            return new PostsResponse(postsPage.getNumberOfElements(), postResponseList);
        }
        return new PostsResponse(0, new ArrayList<>());
    }

    public ResponseEntity<?> getPostsById(Integer id, Principal principal) {
        Post post = postRepository.findPostById(id);
        Post postUser = null;

        if (principal != null) {
            postUser = postRepository.findPostByIdForUser(id, userService.getCurrentUser().getId());
            if (userService.getCurrentUser().getIsModerator() == 1) {
                Post postByIdForModerator = postRepository.findPostByIdForModerator(id);
                return postResponse(postByIdForModerator, id);
            }
        }

        if (post == null && postUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = null;
        try {
            user = userRepository.findByEmail(principal.getName()).get();
        } catch (Exception e) {
            System.out.println("user not found");
        }

        if (postUser != null) {
            return postResponse(postUser, id);
        }

        if (user == null || (!(post.getUser().getId() == user.getId()) && user.getIsModerator() == 0)) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }

        return postResponse(post, id);
    }

    private ResponseEntity<?> postResponse(Post post, Integer id) {
        List<PostComment> commentsList = commentsRepository.findComments(id);
        List<String> tagList = tagsRepository.getTagsByPost(id);
        List<PostCommentsResponse> commentsResponseList = new ArrayList<>();
        for (PostComment c : commentsList) {
            commentsResponseList.add(new PostCommentsResponse(c));
        }
        return new ResponseEntity<>(new PostResponseForList(post, commentsResponseList, tagList), HttpStatus.OK);
    }

    public ResponseEntity<?> getPostForModeration(int offset, int limit, String status) {
        if (userService.getCurrentUser().getIsModerator() == 0) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Post> postsPage;

        if (ModerationStatus.NEW.toString().equals(status)) {
            postsPage = postRepository.findAllPostForModeratorNew(pageable);
        } else {
            postsPage = postRepository.findAllPostForModerator(ModerationStatus.valueOf(status), userService.getCurrentUser().getId(), pageable);
        }

        List<PostResponseForList> postResponseList = postsPage.get().map(PostResponseForList::new).collect(Collectors.toList());

        return new ResponseEntity<>(new PostsResponse(postsPage.getNumberOfElements(), postResponseList), HttpStatus.OK);
    }

    public ResponseEntity<?> getMyPosts(int offset, int limit, String status) {
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Post> postsPage;

        switch (status) {
            case "INACTIVE":
                postsPage = postRepository.findAllMyPosts(ModerationStatus.NEW, 0, userService.getCurrentUser().getId(), pageable);
                break;
            case "PENDING":
                postsPage = postRepository.findAllMyPosts(ModerationStatus.NEW, 1, userService.getCurrentUser().getId(), pageable);
                break;
            case "DECLINED":
                postsPage = postRepository.findAllMyPosts(ModerationStatus.DECLINED, 1, userService.getCurrentUser().getId(), pageable);
                break;
            default:
                postsPage = postRepository.findAllMyPosts(ModerationStatus.ACCEPTED, 1, userService.getCurrentUser().getId(), pageable);
        }

        List<PostResponseForList> postResponseList = postsPage.get().map(PostResponseForList::new).collect(Collectors.toList());

        return new ResponseEntity<>(new PostsResponse(postsPage.getNumberOfElements(), postResponseList), HttpStatus.OK);
    }


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
            User user = userService.getCurrentUser();
            post.setUser(user);
            post.setIsActive(createPost.getActive());
            post.setTime(LocalDateTime.ofEpochSecond(createPost.getTimestamp(), 0, ZoneOffset.UTC));
            post.setTitle(createPost.getTitle());
            post.setText(createPost.getText());

            if (globalSettingsRepository.getSettingsById("POST_PREMODERATION").get(0).getValue().equals("YES")
                    && user.getIsModerator() == 0) {
                post.setModerationStatus(ModerationStatus.NEW);
            }

            if (globalSettingsRepository.getSettingsById("POST_PREMODERATION").get(0).getValue().equals("NO") && createPost.getActive() == 1) {
                post.setModerationStatus(ModerationStatus.ACCEPTED);
            }

            post.setModeratorId(null);
            postRepository.save(post);

            for (String t : createPost.getTags()) {
                Tag tag = new Tag();
                Tags2Post tags2Post = new Tags2Post();
                tag.setName(t);
                tags2Post.setPost(post);
                tags2Post.setTag(tagsRepository.save(tag));
                tags2PostRepository.save(tags2Post);
            }

            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
    }

    //TODO: Удалять только те которых нет в createPost. Ещё время сохранения надо поправить вроде
    public ResponseEntity<?> editPost(int id, Principal principal, CreatePost createPost) {
        Map<PostErrors, String> list = new HashMap<>();

        Post post = postRepository.findPostByIdForUser(id, userService.getCurrentUser().getId());

        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (post.getUser().getId() != userService.getCurrentUser().getId()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (createPost.getText().length() < 3 || createPost.getText().length() > 50) {
            list.put(PostErrors.TEXT, PostErrors.TEXT.getErrors());
        }

        if (createPost.getTitle().length() < 3 || createPost.getTitle().length() > 50) {
            list.put(PostErrors.TITLE, PostErrors.TITLE.getErrors());
        }

        if (!list.isEmpty()) {
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
        }

        Date dateNow = new Date();
        Date datePost = new Date(createPost.getTimestamp());

        if (datePost.before(dateNow)) {
            datePost = dateNow;
        }

        if (userService.getCurrentUser().getIsModerator() == 0) {
            postRepository.updatePost(id, createPost.getTitle(), createPost.getText(), createPost.getActive(),
                    LocalDateTime.ofEpochSecond(datePost.getTime() / 1000, 0, ZoneOffset.UTC), ModerationStatus.NEW);
        } else {
            postRepository.updatePostForModerator(id, createPost.getTitle(), createPost.getText(), createPost.getActive(),
                    LocalDateTime.ofEpochSecond(datePost.getTime() / 1000, 0, ZoneOffset.UTC));
        }

        tags2PostRepository.deleteTagsPyPostId(id);

        for (String tags : createPost.getTags()) {
                Tag tag = new Tag();
                tag.setName(tags);
                Tags2Post tags2Post = new Tags2Post();
                tags2Post.setPost(post);
                tags2Post.setTag(tagsRepository.save(tag));
                tags2PostRepository.save(tags2Post);
        }

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }
}