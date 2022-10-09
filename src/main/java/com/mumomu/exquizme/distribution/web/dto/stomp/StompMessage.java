package com.mumomu.exquizme.distribution.web.dto.stomp;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@NoArgsConstructor
public abstract class StompMessage {
    //@Enumerated(EnumType.STRING)
    //protected Role toWhom; // 어떤 역할에게 보내는 메시지인지
    @Enumerated(EnumType.STRING)
    protected MessageType messageType; // 어떤 메시지인지 구분
    protected String fromSession; // 보내는 세션 아이디
    //protected String message; // 보내는 메세지

    public StompMessage(MessageType messageType, String fromSession) {
        setFlagAndSession(messageType, fromSession);
    }

    public void setFlagAndSession(MessageType messageType, String fromSession){
        this.messageType = messageType;
        this.fromSession = fromSession;
    }
}
