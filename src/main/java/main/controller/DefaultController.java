package main.controller;

import org.springframework.web.bind.annotation.RequestMapping;

public class DefaultController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
