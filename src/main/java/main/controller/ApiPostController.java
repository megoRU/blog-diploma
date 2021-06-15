package main.controller;

import main.dto.responses.PostsResponse;
import main.dto.responses.TagsResponseList;
import main.service.PostService;
import main.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {

    private final PostService postService;
    private final TagService tagService;

    @Autowired
    public ApiPostController(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

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
}
