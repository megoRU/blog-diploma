package main.service;

import lombok.RequiredArgsConstructor;
import main.dto.enums.EnumResponse;
import main.dto.enums.PostErrors;
import main.dto.enums.ReactionsForPost;
import main.dto.request.CommentRequest;
import main.dto.request.CreatePost;
import main.dto.request.PostModerationRequest;
import main.dto.request.ReactionRequest;
import main.dto.responses.*;
import main.model.*;
import main.model.enums.ModerationStatus;
import main.repositories.*;
import main.security.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String dateRegex = "\\d.+-\\d{2}-\\d{2}";
    private final PostRepository postRepository;
    private final TagsRepository tagsRepository;
    private final Tags2PostRepository tags2PostRepository;
    private final CommentsRepository commentsRepository;
    private final UserService userService;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostVoteRepository postVoteRepository;

    public PostsResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postsPage;

        switch (mode) {
            case "popular":
                postsPage = postRepository.findAllPostsByCommentsDesc(pageable);
                break;
            case "best":
                postsPage = new PageImpl<>(postRepository.findAllPostsByVotesDesc(), pageable, postRepository.findAllPostsByVotesDesc().size());
                int total = Integer.parseInt(String.valueOf(postsPage.get().count()));
                int start = toIntExact(pageable.getOffset());
                int end = Math.min((start + pageable.getPageSize()), total);
                List<Post> output = new ArrayList<>();

                if (start <= end) {
                    output = postRepository.findAllPostsByVotesDesc().subList(start, end);
                }

                return getPosts(output, postRepository.findAllPosts().size());
            case "early":
                postsPage = postRepository.findAllPostsByTime(pageable);
                break;
            default:
                postsPage = postRepository.findAllPostsByTimeDesc(pageable);
                break;
        }

        return getPosts(postsPage.toList(), postRepository.findAllPosts().size());
    }

    public PostsResponse getPostsSearch(int offset, int limit, String query) {
        if (query.trim().equals("")) {
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<Post> postsPage = postRepository.findAllPostsByTimeDesc(pageable);
            return getPosts(postsPage.toList(), postRepository.findAllPosts().size());
        }

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postsPage = postRepository.findAllPostsByName(query, pageable);

        List<PostResponseForList> postResponseList = postsPage.get().map(PostResponseForList::new).collect(Collectors.toList());

        return new PostsResponse(postsPage.getNumberOfElements(), postResponseList);
    }

    public PostsResponse getPostsByDate(int offset, int limit, String date) {
        if (date.matches(dateRegex)) {
            Pageable pageable = PageRequest.of(offset / limit, limit);

            LocalDateTime first = LocalDateTime.parse(date + " 00:00", formatter);
            LocalDateTime second = LocalDateTime.parse(date + " 23:59", formatter);

            Page<Post> postsPage = postRepository.findAllPostsByDate(first, second, pageable);
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
                if (postUser != null) {
                    return postResponse(postUser, id);
                }
                Post postByIdForModerator = postRepository.findPostByIdForModerator(id);
                if (postByIdForModerator == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return postResponse(postByIdForModerator, id);
            }
        }

        if (post == null && postUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = null;
        try {
            user = userService.getCurrentUser();
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
        List<String> tagList = tagsRepository.getTagsByPostString(id);
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
                postsPage = postRepository.findAllMyPosts(ModerationStatus.NEW, ModerationStatus.ACCEPTED, 0, userService.getCurrentUser().getId(), pageable);
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
        Map<String, String> list = new HashMap<>();

        if (createPost.getTitle().length() < 3) {
            list.put(EnumResponse.title.name(), PostErrors.TITLE.getErrors());
        }

        if (createPost.getText().length() < 50) {
            list.put(EnumResponse.text.name(), PostErrors.TEXT.getErrors());
        }

        if (list.isEmpty()) {
            Post post = new Post();
            User user = userService.getCurrentUser();
            post.setUser(user);
            post.setIsActive(createPost.getActive());
            post.setTime(LocalDateTime.ofEpochSecond(createPost.getTimestamp(), 0, ZoneOffset.UTC));
            post.setTitle(createPost.getTitle());
            post.setText(createPost.getText());

            if (userService.getCurrentUser().getIsModerator() == 0) {
                if (globalSettingsRepository.getSettingsById("POST_PREMODERATION").getValue().equals("YES")) {
                    post.setModerationStatus(ModerationStatus.ACCEPTED);
                } else { //??.??. ???????????????? ???????????????? ??????
                    post.setModerationStatus(ModerationStatus.NEW);
                }
                post.setModeratorId(null);
            } else {
                //TODO ?????????? id ???????????????????? ??????????????
                post.setModerationStatus(ModerationStatus.ACCEPTED);
            }

            postRepository.save(post);

            for (String t : createPost.getTags()) {
                setTagsToPost(post, t);
            }

            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
    }

    public ResponseEntity<?> editPost(int id, Principal principal, CreatePost createPost) {
        Map<String, String> list = new HashMap<>();

        Post post = postRepository.findPostByIdForModer(id);

        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (post.getUser().getId() != userService.getCurrentUser().getId() && userService.getCurrentUser().getIsModerator() == 0) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (createPost.getTitle().length() < 3) {
            list.put(EnumResponse.title.name(), PostErrors.TITLE.getErrors());
        }

        if (createPost.getText().length() < 50) {
            list.put(EnumResponse.text.name(), PostErrors.TEXT.getErrors());
        }

        if (!list.isEmpty()) {
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
        }

        Date dateNow = new Date();
        Date datePost = new Date(createPost.getTimestamp());

        if (datePost.before(dateNow)) {
            datePost = dateNow;
        }

        post.setTitle(createPost.getTitle());
        post.setText(createPost.getText());
        post.setIsActive(createPost.getActive());
        post.setTime(LocalDateTime.ofEpochSecond(datePost.getTime() / 1000, 0, ZoneOffset.UTC));

        if (userService.getCurrentUser().getIsModerator() == 0
                && globalSettingsRepository.getSettingsById("POST_PREMODERATION").getValue().equals("YES")) {

            post.setModerationStatus(ModerationStatus.NEW);
            post.setModeratorId(null);
            postRepository.save(post);

        } else if (userService.getCurrentUser().getIsModerator() == 0
                && globalSettingsRepository.getSettingsById("POST_PREMODERATION").getValue().equals("NO")) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
            post.setModeratorId(null);
            postRepository.save(post);

        } else {
            postRepository.save(post);
        }

        List<Tags2Post> tags2Posts = tags2PostRepository.getTags2Posts(id);
        HashMap<String, Tags2Post> oldTags = new HashMap<>();

        for (int i = 0; i < tags2Posts.size(); i++) {
            oldTags.put(tags2Posts.get(i).getTag().getName(), tags2Posts.get(i));
        }

        for (String tags : createPost.getTags()) {
            if (!oldTags.containsKey(tags)) {
                setTagsToPost(post, tags);
            } else {
                oldTags.remove(tags);
            }
        }
        //?????????????? ???????????? ???? ?????????????? ???????????? ?? ??????????
        if (!oldTags.isEmpty()) {
            oldTags.forEach((s, tags2Post) -> tags2PostRepository.deleteTags2PostById(tags2Post.getId()));
        }

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    private ResponseEntity<?> saveReaction(PostVote postVote) {
        postVoteRepository.save(postVote);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    public ResponseEntity<?> reactionToPost(ReactionRequest reactionRequest, ReactionsForPost reactionsForPost) {
        PostVote postVote = postVoteRepository.getPostVoteByIdAndByUserId(reactionRequest.getPostId(), userService.getCurrentUser().getId());

        if (postVote == null) {
            postVote = new PostVote();
            Post post = postRepository.findPostById(reactionRequest.getPostId());
            User user = userService.getCurrentUser();
            postVote.setPost(post);
            postVote.setUser(user);
            postVote.setTime(LocalDateTime.ofEpochSecond(Instant.now().getEpochSecond(), 0, ZoneOffset.UTC));
            if (reactionsForPost.equals(ReactionsForPost.LIKE)) {
                postVote.setValue(1);
            } else {
                postVote.setValue(-1);
            }
            return saveReaction(postVote);
        }

        if (reactionsForPost.equals(ReactionsForPost.LIKE)) {
            if (postVote.getValue() != 1) {
                postVote.setValue(1);
                return saveReaction(postVote);
            }
        } else {
            if (postVote.getValue() != -1) {
                postVote.setValue(-1);
                return saveReaction(postVote);
            }
        }

        return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
    }

    public ResponseEntity<?> addComment(CommentRequest commentRequest) {
        Map<String, String> list = new HashMap<>();
        Post post = postRepository.findPostById(commentRequest.getPostId());

        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (commentRequest.getText().length() < 3) {
            list.put(EnumResponse.text.name(), PostErrors.TEXT.getErrors());
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.BAD_REQUEST);
        }

        PostComment postComment = new PostComment(post, userService.getCurrentUser(), commentRequest.getParentId(),
                commentRequest.getText(), LocalDateTime.ofEpochSecond(Instant.now().getEpochSecond(), 0, ZoneOffset.UTC));

        commentsRepository.save(postComment);

        return new ResponseEntity<>(new CommentResponse(postComment.getId()), HttpStatus.OK);
    }

    public ResponseEntity<?> postModeration(PostModerationRequest postModerationRequest) {
        if (userService.getCurrentUser().getIsModerator() == 0) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Post post = postRepository.findPostByIdForModerator(postModerationRequest.getPostId());

        if (post == null) {
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }

        if (postModerationRequest.getDecision().equals("accept")) {
            return getResponse(post, ModerationStatus.ACCEPTED);
        } else {
            return getResponse(post, ModerationStatus.DECLINED);
        }

    }

    private ResponseEntity<?> getResponse(Post post, ModerationStatus moderationStatus) {
        post.setModerationStatus(moderationStatus);
        post.setModeratorId(userService.getCurrentUser().getId());
        postRepository.save(post);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    private void setTagsToPost(Post post, String t) {
        Tag getTag = tagsRepository.getTagIdByName(t);
        Tags2Post tags2Post = new Tags2Post();
        tags2Post.setPost(post);

        if (getTag == null) {
            Tag tag = new Tag();
            tag.setName(t);
            tags2Post.setTag(tagsRepository.save(tag));
        } else {
            tags2Post.setTag(tagsRepository.save(getTag));
        }
        tags2PostRepository.save(tags2Post);
    }

    private PostsResponse getPosts(List<Post> postsPages, int size) {
        List<PostResponseForList> postResponseList = postsPages.stream().map(PostResponseForList::new).collect(Collectors.toList());
        return new PostsResponse(size, postResponseList);
    }
}