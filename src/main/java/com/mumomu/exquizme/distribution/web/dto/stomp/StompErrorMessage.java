package com.mumomu.exquizme.distribution.web.dto.stomp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StompErrorMessage extends StompMessage{
    private String errorMessage;

    public StompErrorMessage(MessageType messageType, String fromSession, String errorMessage) {
        super(messageType, fromSession);
        this.errorMessage = errorMessage;
    }
}
