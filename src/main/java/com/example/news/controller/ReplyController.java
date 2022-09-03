package com.example.news.controller;

import com.example.news.auth.Auth;
import com.example.news.auth.User;
import com.example.news.auth.UserDetails;
import com.example.news.dto.req.ReplyInDto;
import com.example.news.dto.res.ReplyOutDto;
import com.example.news.entity.ReplyEntity;
import com.example.news.exception.ErrorCode;
import com.example.news.exception.ForbiddenException;
import com.example.news.exception.RestApiException;
import com.example.news.repo.ReplyRepo;
import com.example.news.service.ReplyService;
import com.example.news.types.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Tag(name = "뉴스 답글", description = "뉴스 답글 관련 api")
@RestController
@RequestMapping("/api/reply")
public class ReplyController {

    @Autowired
    ReplyService replyService;

    @Autowired
    ReplyRepo replyRepo;

    @Operation(summary = "댓글 생성", description = "댓글을 생성한다. 유저 권한 필요")
    @Auth(userRole = UserRole.USER)
    @PostMapping("/create")
    public ReplyOutDto createReply(@User UserDetails userDetails, @RequestBody @Valid ReplyInDto reply) {

        return replyService.createReply(reply, userDetails.getUserId());
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제한다. 유저 권한 필요")
    @Auth(userRole = UserRole.USER)
    @DeleteMapping("/replyId")
    public void deleteReply(@User UserDetails userDetails, @PathVariable int replyId) {

        checkOwner(userDetails, replyId);
        replyService.deleteReply(replyId);
    }

    //뉴스 주인 유저인지 확인하는 로직
    private void checkOwner(UserDetails userDetails, int replyId){

        //어드민이면 무조건 통과
        if(userDetails.getUserRole() == UserRole.ADMIN) return;

        Optional<ReplyEntity> replyEntity = replyRepo.findByReplyId(replyId);

        //항목이 없으면 에러
        if(replyEntity.isEmpty()) throw new RestApiException(ErrorCode.NO_MATCH_ITEM_ERROR);

        //주인이 아니면 에러
        if(replyEntity.get().getUser().getUserId() != userDetails.getUserId()) throw new ForbiddenException(ErrorCode.NO_AUTHORIZATION_ERROR);

    }
}
