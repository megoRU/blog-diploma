package main.controller;

import main.dto.responses.InitResponse;
import main.dto.responses.PostsResponse;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {

    //Доделать
    private final InitResponse initResponse;
    private final PostService postService;

    @Autowired
    public ApiPostController(PostService postService, InitResponse initResponse) {
        this.postService = postService;
        this.initResponse = initResponse;
    }

    @GetMapping("/api/post")
    public ResponseEntity<PostsResponse> getPosts(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "recent") String mode) {

        return ResponseEntity.ok(postService.getPosts(offset, limit, mode));
    }


    @GetMapping("/api/tag")
    private InitResponse tag() {
        return initResponse;
    }
}
