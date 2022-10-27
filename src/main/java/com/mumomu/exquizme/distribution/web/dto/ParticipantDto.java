package com.mumomu.exquizme.distribution.web.dto;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder @AllArgsConstructor
public class ParticipantDto {
    private Long id;
    private String sessionId;
    private String name; // 사용자 구분 이름
    private String nickname;
    private Date entryDate; // 생성일(입장시간)
    private int currentScore; // 점수
    private int imageNumber;
    private int colorNumber;

    public ParticipantDto(Participant participant) {
        this.id = participant.getId();
        this.sessionId = participant.getSessionId();
        this.name = participant.getName();
        this.nickname = participant.getNickname();
        this.entryDate = participant.getEntryDate();
        this.currentScore = participant.getCurrentScore();
        this.imageNumber = participant.getImageNumber();
        this.colorNumber = participant.getColorNumber();
    }
}
