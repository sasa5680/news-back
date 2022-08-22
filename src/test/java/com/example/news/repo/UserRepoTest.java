package com.example.news.repo;
import com.example.news.config.JpaAuditingConfig;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    @DisplayName("유저 엔티티 저장 테스트")
    @Test
    public void saveTest(){

        UserEntity userEntity = MockUser.getUserEntity();
        UserEntity savedEntity = userRepo.save(userEntity);

        assertEquals(userEntity.getUserEmail(), savedEntity.getUserEmail());
        assertEquals(userEntity.getUserPw(), savedEntity.getUserPw());
        assertEquals(userEntity.getUserName(), savedEntity.getUserName());
        assertEquals(userEntity.getUserRole(), savedEntity.getUserRole());
        assertEquals(userEntity.getUserIntro(), savedEntity.getUserIntro());
        assertEquals(userEntity.getUserProfile(), savedEntity.getUserProfile());
        assertEquals(false, savedEntity.isDeleted());
        assertNotNull(userEntity.getCreatedAt());
        assertNotNull(userEntity.getModifiedAt());

    }

    @DisplayName("유저 메일로 이름 찾아오는 테스트")
    @Test
    public void findByUserEmailTest() {

        UserEntity userEntity = MockUser.getUserEntity();
        UserEntity savedEntity = userRepo.save(userEntity);

        UserEntity findEntity = userRepo.findByUserEmail(savedEntity.getUserEmail());

        assertNotNull(findEntity);
    }

    @DisplayName("유저 메일로 이름 찾아오는 테스트")
    @Test
    public void findByUserEmailAndPwTest(){

        UserEntity userEntity = MockUser.getUserEntity();
        UserEntity savedEntity = userRepo.save(userEntity);

        //성공 케이스스
       boolean res = userRepo.existsByUserEmailAndUserPw(savedEntity.getUserEmail(), savedEntity.getUserPw());

        assertEquals(true, res);

    }
}
