package com.example.news.controller;

import com.example.news.auth.Auth;
import com.example.news.auth.User;
import com.example.news.auth.UserDetails;
import com.example.news.types.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @Auth(userRole = UserRole.USER)
    @GetMapping("/auth")
    public String auth() { return "auth";}

    @Auth(userRole = UserRole.ADMIN)
    @GetMapping("/admin")
    public String adminUser() {
        return "admin";
    }

    @Auth(userRole = UserRole.USER)
    @GetMapping("/userdetails")
    public UserDetails authUser(@User UserDetails userDetails) {
        return userDetails;
    }
}
