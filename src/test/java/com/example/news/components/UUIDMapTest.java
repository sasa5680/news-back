package com.example.news.components;

import com.example.news.component.UUIDMap;
import com.example.news.token.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
//@Import(
//        value = {
//                TokenProvider.class
//        }
//)
@ContextConfiguration(classes = UUIDMap.class)
public class UUIDMapTest {
}
