package com.example.news.dto.req;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@ToString
@Builder
@Schema(description = "로그인 dto")
public class LoginDto {

    @Email
    private String userEmail;

    @NotEmpty
    private String userPw;
}
