package com.example.news.components;

import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockUser;
import com.example.news.token.TokenProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(SpringExtension.class)
//@Import(
//        value = {
//                TokenProvider.class
//        }
//)
@ContextConfiguration(classes = TokenProvider.class)
@TestPropertySource(
                properties = {
        "jwt.secret=c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK",
        "jwt.token-validity-in-seconds=84600"
})
//@TestPropertySource(properties = {"spring.config.location = classpath:application.yml"})
public class TokenProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @Test
    public void tokenTest(){

        int userId = 1;
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity.setUserId(userId);

        String token = tokenProvider.createToken(userEntity.getUserRole(), userId);

        assertNotNull(token);
        assertEquals(userEntity.getUserId(), tokenProvider.getUserIdFromToken(token));
        assertEquals(userEntity.getUserRole().getUserRoleType(), tokenProvider.getUserRoleFromToken(token));
    }
}
