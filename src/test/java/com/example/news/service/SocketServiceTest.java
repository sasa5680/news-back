package com.example.news.service;

import com.example.news.dto.res.NewNewsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SocketServiceTest {

    //서버 포트 추소
    @Value("${local.server.port}")
    private int port;

    //소켓 연결할 주소
    private String URL;

    //소켓통신할 URL
    private final static String NEW_NEWS_ENDPOINT = "/topic/new";

    private CompletableFuture<NewNewsDto> completableFuture;

    @Autowired
    SocketService socketService;

    @BeforeEach
    public void setup() {
        completableFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/socket";
    }

    @Test
    public void newNewsAlarmTest() throws ExecutionException, InterruptedException, TimeoutException {

        //클라이언트 생성
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        //서버와 연결
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        //수신 시작
        stompSession.subscribe(NEW_NEWS_ENDPOINT , new CreateStompFrameHandler());

        //given
        String newsTitle = "newsTitle";
        String newsId = "1";

        NewNewsDto newNewsDto = new NewNewsDto();
        newNewsDto.setNewsId(newsId);
        newNewsDto.setNewsTitle(newsTitle);

        //서버에서 송신
        //when
        socketService.sendMessage(newNewsDto);

        //then
        //수신
        NewNewsDto received = completableFuture.get(10, SECONDS);
        System.out.println(received);

        assertEquals(newsTitle, received.getNewsTitle());
        assertEquals(newsId, received.getNewsId());

    }

    //
    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    //클라이언트에 데이터 들어왔을 경우 핸들러
    private class CreateStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return NewNewsDto.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((NewNewsDto) o);
        }
    }
}
