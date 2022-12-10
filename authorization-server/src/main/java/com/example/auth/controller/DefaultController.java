package com.example.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    /**
     * Only for testing to make sure the server started successfully.
     */
    @GetMapping("/")
    public String index() {
        return "this is the oauth2 auth server.";
    }
}
