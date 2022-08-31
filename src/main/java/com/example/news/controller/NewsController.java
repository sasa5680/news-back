package com.example.news.controller;


import com.example.news.auth.Auth;
import com.example.news.auth.User;
import com.example.news.auth.UserDetails;
import com.example.news.dto.req.NewsInDto;
import com.example.news.dto.res.NewsOutDto;
import com.example.news.dto.res.NewsSimpleDto;
import com.example.news.exception.ErrorCode;
import com.example.news.exception.ForbiddenException;
import com.example.news.repo.NewsRepo;
import com.example.news.service.NewsService;
import com.example.news.types.NewsMain;
import com.example.news.types.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

@Tag(name = "뉴스 게시물", description = "뉴스 게시물 관련 api")
@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    NewsService newsService;

    @Autowired
    NewsRepo newsRepo;

    //뉴스 작성
    @Operation(summary = "뉴스 생성", description = "뉴스를 생성한다. 기자 권한 필요")
    @Auth(userRole = UserRole.WRITER)
    @PostMapping("/create")
    public ResponseEntity<NewsOutDto> createNews
    (
            @User UserDetails userDetails,
            @Valid NewsInDto newsInDto ) throws IOException
    {
        NewsOutDto newsOutDto = newsService.createNews(userDetails.getUserId(), newsInDto);

        return new ResponseEntity<>(newsOutDto, HttpStatus.CREATED);
    }

    //뉴스 조회
    @Operation(summary = "뉴스 읽기", description = "뉴스를 읽어온다.")
    @GetMapping("/{id}")
    public NewsOutDto readNews(@PathVariable int id){

        return newsService.readNews(id);
    }

    //뉴스 조회
    @Operation(summary = "전체 뉴스를 가져온다.", description = "뉴스를 읽어온다.")
    @GetMapping("")
    public Page<NewsSimpleDto> readAllNews( @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return newsService.readAllNews(pageable);
    }

    @Operation(summary = "뉴스를 검색 조건에 따라 가져온다. 유저 전용")
    @GetMapping("/usernews/{userName}")
    public Page<NewsSimpleDto> readByUserName(
            @Parameter(description = "유저명", in = ParameterIn.PATH)
            @PathVariable String userName,
            @PageableDefault(page = 0, size = 10) Pageable pageable){

        return newsService.readByUserName(userName, pageable);
    }

    @Operation(summary = "뉴스를 검색 조건에 따라 가져온다. 유저 전용")
    @GetMapping("/user")
    public Page<NewsSimpleDto> readByUser(
            @Parameter(description = "검색어, null 혹은 빈 문자열 가능", in = ParameterIn.QUERY)
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "검색어, null 혹은 빈 문자열 가능", in = ParameterIn.QUERY)
            @RequestParam(value = "cate", required = false) String newsCate,
            @Parameter(description = "뉴스 메인 여부, null 혹은 빈 문자열 가능", in = ParameterIn.QUERY)
            @RequestParam(value = "main", required = false) String newsMain,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        return newsService.readBySearch(true, query, newsCate, newsMain, pageable);
    }

    @Operation(summary = "뉴스를 검색 조건에 따라 가져온다. 승인되지 않은것도 가져온다. 어드민 전용")
    @Auth(userRole = UserRole.ADMIN)
    @GetMapping("/admin")
    public Page<NewsSimpleDto> readByAdmin(
            @Parameter(description = "뉴스 승인됨 여부 null 가능", in = ParameterIn.QUERY)
            @RequestParam(value = "approved", required = false) Boolean approved,
            @Parameter(description = "검색어, null 혹은 빈 문자열 가능", in = ParameterIn.QUERY)
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "검색어, null 혹은 빈 문자열 가능", in = ParameterIn.QUERY)
            @RequestParam(value = "cate", required = false) String newsCate,
            @Parameter(description = "뉴스 메인 여부, null 혹은 빈 문자열 가능", in = ParameterIn.QUERY)
            @RequestParam(value = "main", required = false) String newsMain,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        return newsService.readBySearch(approved, query, newsCate, newsMain, pageable);
    }

    //메인 뉴스들 가져온다.
    @Operation(summary = "뉴스를 검색 조건에 따라 가져온다. 승인되지 않은것도 가져온다. 어드민 전용")
    @GetMapping("/main")
    public Set<NewsSimpleDto> readByAdmin() {

        return newsService.readMainNews();
    }


    //뉴스 수정
    @Operation(summary = "수정", description = "뉴스를 수정한다. 어드민 권한, 소유자 권한 필요")
    @Auth(userRole = UserRole.WRITER)
    @PutMapping("/{id}")
    public NewsOutDto updateNews(
            @User
            UserDetails userDetails,
            @PathVariable int id,
            @Valid NewsInDto newsInDto) throws IOException {

        if(!checkOwner(userDetails, id)) throw new ForbiddenException(ErrorCode.NO_AUTHORIZATION_ERROR);
        
        return newsService.updateNews(id, newsInDto);
    }

    @Operation(summary = "뉴스 삭제", description = "뉴스를 삭제한다. 권한 필요")
    @Auth(userRole = UserRole.WRITER)
    @DeleteMapping("/{id}")
    public void deleteNews(
            @User
            UserDetails userDetails,
            @PathVariable int id
    ) throws IOException {

        if(!checkOwner(userDetails, id)) throw new ForbiddenException(ErrorCode.NO_AUTHORIZATION_ERROR);
        newsService.deleteNews(id);
    }

    @Operation(summary = "뉴스 승인", description = "뉴스를 승인한다. 권한 필요")
    @Auth(userRole = UserRole.ADMIN)
    @PutMapping("/approve/{id}")
    public NewsOutDto approveNews(
            @User
            UserDetails userDetails,
            @Parameter(description = "뉴스 고유 아이디", required = true, in = ParameterIn.PATH)
            @PathVariable int id,
            @Parameter(description = "뉴스 승인 여부", required = true, in = ParameterIn.QUERY)
            @RequestParam boolean approved
    ) throws IOException {

        return newsService.setNewsApproved(id, approved);
    }

    @Operation(summary = "뉴스 메인 변경", description = "뉴스의 메인 여부를 수정한다.운영자 권한 필요")
    @Auth(userRole = UserRole.ADMIN)
    @PutMapping("/newsmain/{id}")
    public NewsOutDto setNewsMain(
            @User
            UserDetails userDetails,
            @Parameter(description = "뉴스 고유 아이디", required = true, in = ParameterIn.PATH)
            @PathVariable int id,
            @Parameter(description = "뉴스 승인 여부", required = true, in = ParameterIn.QUERY)
            @RequestParam NewsMain newsmain
    ) throws Exception {

        return newsService.setNewsMain(id, newsmain);
    }


    //뉴스 주인 유저인지 확인하는 로직
    private boolean checkOwner(UserDetails userDetails, int newsId){

        //어드민이면 무조건 통과
        if(userDetails.getUserRole() == UserRole.ADMIN) return true;
        //뉴스 주인이면 통과
        if(newsRepo.findByNewsId(newsId).getUser().getUserId() == userDetails.getUserId()) return true;

        return false;
    }
}
