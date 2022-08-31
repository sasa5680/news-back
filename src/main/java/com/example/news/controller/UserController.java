package com.example.news.controller;

import com.example.news.auth.Auth;
import com.example.news.auth.User;
import com.example.news.auth.UserDetails;
import com.example.news.dto.req.UserInDto;
import com.example.news.dto.req.UserUpdateDto;
import com.example.news.dto.res.UserOutDto;
import com.example.news.exception.ErrorCode;
import com.example.news.exception.ForbiddenException;
import com.example.news.service.MailService;
import com.example.news.service.UserService;
import com.example.news.types.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @Operation(summary = "유저 생성", description = "유저를 생성한다.")
    @PostMapping("/create")
    public String createUser(
            @Parameter(name = "유저 정보 dto", description = "유저 DTO", required = true)
            @Valid UserInDto userInDto) throws IOException, MessagingException {

        return userService.acceptSignUp(userInDto);
    }

    @Operation(summary = "유저 읽어오기", description = "유저 고유 id를 이용하여 유저를 읽어온다.")
    @GetMapping("/{userName}")
    public UserOutDto readUser(
            @Parameter(name = "유저 네임", description = "유저 고유 이름", in = ParameterIn.PATH)
            @PathVariable("userName") String userName)
    {
        return userService.readUser(userName);
    }

    @Operation(summary = "유저 수정하기", description = "유저 고유 id를 이용하여 유저를 수정한다.")
    @Auth(userRole = UserRole.USER)
    @PutMapping("/{id}")
    public UserOutDto updateUser(
            @User UserDetails userDetails,
            @Parameter(name = "id", description = "유저 고유 id", in = ParameterIn.PATH)
            @PathVariable("id") int id,
            @Parameter(name = "user update dto", description = "유저 업데이트 dto")
            @Valid UserUpdateDto userUpdateDto) throws IOException {
        return userService.updateUser(userDetails.getUserId(), userUpdateDto);
    }

    @Operation(summary = "유저 삭제하기", description = "유저 고유 id를 이용하여 유저를 삭제한다.")
    @Auth(userRole = UserRole.USER)
    @DeleteMapping("/{id}")
    public UserOutDto deleteUser(
            @User UserDetails userDetails,
            @Parameter(name = "id", description = "유저 고유 id", in = ParameterIn.PATH)
            @PathVariable("id") int id
            ) throws IOException {

        return userService.deleteUser(id);
    }

    @Operation(summary = "이메일 중복 체크", description = "이메일이 중복되었는지 확인")
    @GetMapping("/email/{email}")
    public void dupEmailCheck(
            @Parameter(name = "email", description = "중복 확인할 이메일", in = ParameterIn.PATH)
            @PathVariable String email
    )  {
        if (userService.isDupEmail(email)) throw new ForbiddenException(ErrorCode.ALREADY_EMAIL_ERROR);
    }

    @Operation(summary = "유저명 중복 체크", description = "유저명이 중복되었는지 확인")
    @GetMapping("/name/{name}")
    public void dupNameCheck(
            @Parameter(name = "name", description = "중복 확인할 유저명", in = ParameterIn.PATH)
            @PathVariable String name
    )  {

        if (userService.isDupName(name)) throw new ForbiddenException(ErrorCode.ALREADY_NICKNAME_ERROR);
    }

    @Operation(summary = "이메일 확인", description = "이메일의 코드를 확인한다.")
    @GetMapping("/confirm/{uuid}")
    public UserOutDto confirmCode(
            @Parameter(name = "uuid", description = "확인할 코드", in = ParameterIn.PATH)
            @PathVariable String uuid
    ) throws MessagingException, IOException {

        Optional<UserOutDto> userOutDto =  userService.emailVerifyUUID(uuid);

        System.out.println(userOutDto.isPresent());
        if(userOutDto.isPresent()) return userOutDto.get();
        else throw new ForbiddenException(ErrorCode.EMAIL_NOT_CERTIFIED);
    }
}
