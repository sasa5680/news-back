package com.example.news.service;

import com.example.news.bean.ModelMapperBean;
import com.example.news.component.UUIDMap;
import com.example.news.dto.in.UserInDto;
import com.example.news.dto.in.UserUpdateDto;
import com.example.news.dto.out.UserOutDto;
import com.example.news.entity.UserEntity;
import com.example.news.exception.BadRequestException;
import com.example.news.exception.ErrorCode;
import com.example.news.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    ModelMapperBean modelMapperBean;

    @Autowired
    S3Service s3Service;

    @Autowired
    MailService mailService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UUIDMap uuidMap;

    @Autowired
    SignUpCodeService signUpCodeService;

    @Transactional
    public String acceptSignUp(UserInDto userInDto) throws IOException, MessagingException {

        if(userRepo.existsByUserEmail(userInDto.getUserEmail()))
            throw new BadRequestException(ErrorCode.ALREADY_EMAIL_ERROR);

        if(userRepo.existsByUserName(userInDto.getUserName()))
            throw new BadRequestException(ErrorCode.ALREADY_EMAIL_ERROR);

        //UUID 생성
        UUID uuid = java.util.UUID.randomUUID();

        //map 에 저장
        uuidMap.putObject(uuid.toString(), userInDto);

        //메일 전송
        mailService.sendConfirmEmail(uuid.toString(), userInDto.getUserEmail(), userInDto.getUserName());

        return uuid.toString();
    }

    public UserOutDto readUser(String userName){

        UserEntity userEntity = userRepo.findByUserName(userName);
        return UserOutDto.from(userEntity, modelMapperBean.modelMapper());
    }

    @Transactional
    public UserOutDto updateUser(int id, UserUpdateDto userUpdateDto) throws IOException {

        UserEntity userEntity = userRepo.findByUserId(id);

        String userProfile;
        if(userUpdateDto.getUserProfile() != null){
            userProfile = s3Service.upload(userUpdateDto.getUserProfile());
            s3Service.delete(userEntity.getUserProfile());
        } else {
            userProfile = userEntity.getUserProfile();
        }

        userEntity.setUserName(userUpdateDto.getUserName());
        userEntity.setUserIntro(userUpdateDto.getUserIntro());
        userEntity.setUserProfile(userProfile);

        userRepo.save(userEntity);

        return UserOutDto.from(userEntity, modelMapperBean.modelMapper());
    }

    @Transactional
    public UserOutDto deleteUser(int userId) {
        UserEntity userEntity = userRepo.findByUserId(userId);

        userEntity.setDeleted(true);
        userRepo.save(userEntity);

        return UserOutDto.from(userEntity, modelMapperBean.modelMapper());
    }

    //이메일의 UUID 확인
    public Optional<UserOutDto> emailVerifyUUID(String UUID) throws MessagingException, IOException {

        //해시맵에서 UUID Key 확인
        Optional<UserInDto> userInDto = uuidMap.getObject(UUID);

        //있으면 가입 진행하고 true 리턴
        if(userInDto.isPresent()){

            UserInDto dto = userInDto.get();
            String userProfile;

            if(dto.getUserProfile() != null){
                userProfile = s3Service.upload(dto.getUserProfile(), uuidMap.getFile(UUID).get());
            } else {
                userProfile = "default";
            }

            UserEntity userEntity = UserEntity.from(dto, userProfile, modelMapperBean.modelMapper());
            userEntity = userRepo.save(userEntity);
            UserOutDto userOutDto = UserOutDto.from(userEntity, modelMapperBean.modelMapper());
            System.out.println(userOutDto);
            return Optional.of(userOutDto);
        } else {
            return Optional.ofNullable(null);
        }
    }


    public boolean isDupEmail(String email){
        return userRepo.existsByUserEmail(email);
    }

    public boolean isDupName(String name) {return userRepo.existsByUserName(name);}
}
