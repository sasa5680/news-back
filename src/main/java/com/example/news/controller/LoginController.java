package com.example.news.controller;

import com.example.news.dto.in.LoginDto;
import com.example.news.dto.out.LoginResDto;
import com.example.news.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "로그인", description = "로그인 api")
@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    LoginService loginService;

    @Operation(summary = "이메일 로그인", description = "이메일로 로그인한다.")
    @PostMapping("/email")
    public LoginResDto emailLogin(@RequestBody @Valid LoginDto loginDto){

        return loginService.login(loginDto);
    }
}
