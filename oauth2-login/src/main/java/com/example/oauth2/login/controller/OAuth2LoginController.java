package com.example.oauth2.login.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2LoginController {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginController.class);

    @GetMapping("/")
    public String index(){
        String auth = SecurityContextHolder.getContext().getAuthentication().toString();
        logger.info("Authentication = {}", auth);

        return "Login success. Your auth = " + auth;
    }
}
