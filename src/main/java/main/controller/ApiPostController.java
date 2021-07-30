package main.controller;

import lombok.RequiredArgsConstructor;
import main.dto.enums.ReactionsForPost;
import main.dto.request.CommentRequest;
import main.dto.request.CreatePost;
import main.dto.request.ReactionRequest;
import main.dto.responses.PostsResponse;
import main.dto.responses.TagsResponseList;
import main.service.PostService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class ApiPostController {

    private final PostService postService;
    private final TagService tagService;

    @GetMapping("/api/post")
    public ResponseEntity<PostsResponse> getPosts(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "recent") String mode) {

        return ResponseEntity.ok(postService.getPosts(offset, limit, mode));
    }

    @GetMapping("/api/tag")
    private ResponseEntity<TagsResponseList> tag(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(tagService.getTags(name));
    }

    @GetMapping("/api/post/search")
    private ResponseEntity<PostsResponse> getPostsByName(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(postService.getPostsSearch(offset, limit, query));
    }

    @GetMapping("/api/post/byDate")
    private ResponseEntity<PostsResponse> getPostsByDate(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "") String date) {
        return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/api/post/byTag")
    private ResponseEntity<PostsResponse> getPostsByTag(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "") String tag) {
        return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/api/post/{ID}")
    private ResponseEntity<?> getPostsById(@PathVariable int ID, Principal principal) {
        return postService.getPostsById(ID, principal);
    }

    @GetMapping("/api/post/moderation")
    private ResponseEntity<?> getPostForModeration(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "NEW") String status,
            Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return postService.getPostForModeration(offset, limit, status.toUpperCase(Locale.ROOT));
    }

    @GetMapping("/api/post/my")
    private ResponseEntity<?> getMyPosts(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "PUBLISHED") String status,
            Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return postService.getMyPosts(offset, limit, status.toUpperCase(Locale.ROOT));
    }

    @PostMapping("/api/post")
    private ResponseEntity<?> createPost(@RequestBody CreatePost createPost, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return postService.createPost(createPost);
    }

    @PutMapping("/api/post/{ID}")
    private ResponseEntity<?> editPost(@PathVariable int ID,
                                       Principal principal,
                                       @RequestBody CreatePost createPost) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return postService.editPost(ID, principal, createPost);
    }

    @PostMapping("/api/post/like")
    private ResponseEntity<?> likeToPost(@RequestBody ReactionRequest reactionRequest, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return postService.reactionToPost(reactionRequest, ReactionsForPost.LIKE);
    }

    @PostMapping("/api/post/dislike")
    private ResponseEntity<?> dislikeToPost(@RequestBody ReactionRequest reactionRequest, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return postService.reactionToPost(reactionRequest, ReactionsForPost.DISLIKE);
    }

    @PostMapping("/api/comment")
    private ResponseEntity<?> comment(@RequestBody CommentRequest commentRequest, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return postService.addComment(commentRequest);
    }

}
