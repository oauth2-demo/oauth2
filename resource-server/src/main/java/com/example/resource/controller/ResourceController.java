package com.example.resource.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @PreAuthorize("hasAuthority('SCOPE_read')")
    @GetMapping("/")
    public String index(){
        return "this is a message from resource server.";
    }
}
