package com.example.news.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "에러 dto, 에러 메사지들을 배열의 형태로 리턴")
public class ErrorDto {

    Set<String> messages;
}
