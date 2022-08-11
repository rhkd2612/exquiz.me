package com.mumomu.exquizme.distribution.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RoomHandler extends TextWebSocketHandler {
    private static List<WebSocketSession> wsSessionList = new ArrayList<>();
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // payload란 보내는 패킷의 전송되는 데이터, 헤더와 메타데이터 등 여러 전송의 안정성을 높이는데 도움되는 것을 제외한 데이터 자체
        String payload = message.getPayload();
        log.info("payload : " + payload);

        for (WebSocketSession s: wsSessionList) {
            s.sendMessage(message);
        }
    }

    // Client가 접속 시 호출되는 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(session + "클라이언트 접속");
        wsSessionList.add(session);
    }

    // Client가 접속 해제 시 호출되는 메서드
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session + "클라이언트 탈주");
        wsSessionList.remove(session);
    }
}
