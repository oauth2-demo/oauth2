package com.example.oauth2.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@RestController
public class OAuth2ClientController {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2ClientController.class);

    @Autowired
    private WebClient webClient;


    @GetMapping("/")
    public String authCode() {
        logger.info("Start to retrieve message from resource server by WebClient...");
        String result = webClient
                .get()
                .uri("http://127.0.0.1:8090/") // The URL of "Resource Server"
                .attributes(clientRegistrationId("authorization-server-1"))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        logger.info("result (from resource server) = {}", result);

        return result;
    }






}
