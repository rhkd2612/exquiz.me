package com.mumomu.exquizme.distribution.web.dto.stomp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StompStopMessage extends StompMessage{
    public StompStopMessage(MessageType messageType) {
        super(messageType, null);
    }
}
