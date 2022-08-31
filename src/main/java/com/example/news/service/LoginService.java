package com.example.news.service;

import com.example.news.bean.ModelMapperBean;
import com.example.news.dto.req.LoginDto;
import com.example.news.dto.res.LoginResDto;
import com.example.news.entity.UserEntity;
import com.example.news.exception.ErrorCode;
import com.example.news.exception.NoAuthException;
import com.example.news.repo.UserRepo;
import com.example.news.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    ModelMapperBean modelMapperBean;

    public LoginResDto login(LoginDto loginDto) {

        System.out.println(loginDto.toString());
        //유저 있는지 검사
        UserEntity userEntity = userRepo.findByUserEmail(loginDto.getUserEmail());
        if(userEntity == null) throw new NoAuthException(ErrorCode.LOGIN_FAIL_ERROR);

        if(!userEntity.getUserPw().equals(loginDto.getUserPw()))
            throw new NoAuthException(ErrorCode.LOGIN_FAIL_ERROR);

        String token = tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        return LoginResDto.from(token, userEntity, modelMapperBean.modelMapper());
    }

//    public LoginResDto googleLogin(String tokenId){
//
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + tokenId;
//
//        //구글 유저를 가져온다.
//        ResponseEntity<GoogleUserDto> response
//                = restTemplate.getForEntity(url, GoogleUserDto.class);
//
//        GoogleUserDto googleUserDto =  response.getBody();
//
//        String email = googleUserDto.getEmail();
//
//        //유저가 있으면
//        if(userRepo.existsByUserEmail(email)){
//            UserEntity userEntity = userRepo.findByUserEmail(email);
//            String token = tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());
//            return LoginResDto.from(token, userEntity, modelMapperBean.modelMapper());
//            //없으면 회원가입 처리
//        } else {
//            UserEntity userEntity =  UserEntity.snsFrom(email, passwordEncoder);
//            userRepository.save(userEntity);
//            String token = tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());
//
//            return LoginResultDto.from(userEntity, tokenProvider);
//        }
//    }

}
