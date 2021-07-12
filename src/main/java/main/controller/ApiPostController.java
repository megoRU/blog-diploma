package main.controller;

import lombok.AllArgsConstructor;
import main.api.responses.InitResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ApiPostController {

    //Доделать
    private final InitResponse initResponse;

    @GetMapping("/api/post")
    @PreAuthorize("hasAuthority('user:write')")
    private InitResponse post() {
        return initResponse;
    }

    @GetMapping("/api/tag")
    private InitResponse tag() {
        return initResponse;
    }
}
