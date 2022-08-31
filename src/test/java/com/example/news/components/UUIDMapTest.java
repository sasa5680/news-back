package com.example.news.components;

import com.example.news.component.UUIDMap;
import com.example.news.dto.req.UserInDto;
import com.example.news.mockData.MockUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@Import(
        value = {
                UUIDMap.class
        }
)
public class UUIDMapTest {

    @Autowired
    UUIDMap uuidMap;

    @Test
    public void insertTest() throws IOException {

        //없는 케이스
        Optional<UserInDto> userInDtoEmpty =  uuidMap.getObject("");
        assertEquals(true, userInDtoEmpty.isEmpty());

        //있는 케이스
        UUID uuid = java.util.UUID.randomUUID();
        uuidMap.putObject(uuid.toString(), MockUser.getUserInDto());

        Optional<UserInDto> userInDtoTrue =  uuidMap.getObject(uuid.toString());

        assertEquals(false, userInDtoTrue.isEmpty());

        Optional<UserInDto> userInDtoOptional = uuidMap.getObject(uuid.toString());

        System.out.println(userInDtoOptional);
    }
}
