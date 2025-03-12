package org.example.userservice.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @RequestMapping("/")
    public String hello() {
        return "hello";
    }
}
