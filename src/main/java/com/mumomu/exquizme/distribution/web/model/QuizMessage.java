package com.mumomu.exquizme.distribution.web.model;

import com.mumomu.exquizme.distribution.domain.Participant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizMessage {
    public enum MessageType {
        ENTER,      // 퀴즈방 입장
        TALK,       // 대화
        WHISPER,    // 귓속말
        SOLVE,       // 문제 해결
        OUT         // 나가기
    }

    @ApiModelProperty(value = "메시지 타입", example = "ENTER")
    //@Enumerated(EnumType.STRING)
    private MessageType type;

    @ApiModelProperty(value = "퀴즈방 핀번호", example = "100000")
    private String roomPin;

    // Participant
    @ApiModelProperty(value = "보내는 사람", example = "김민겸")
    private String sender;

//    @ApiModelProperty(value = "받는 사람", example = "임준현")
//    // 특정 사용자에게만 메시지를 전달할 때 사용 (예: 귓속말)
//    private String receiver;

    @ApiModelProperty(value = "메시지", example = "이상빈 님이 입장하였습니다.")
    private String message;

    @ApiModelProperty(value = "퀴즈 풀이 메시지", example = "json형태")
    private Object solvingMessage;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void newConnect() {
        this.type = MessageType.ENTER;
    }

    public void closeConnect(){
        this.type = MessageType.OUT;
    }
}

