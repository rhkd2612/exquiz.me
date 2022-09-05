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
    private RoomDto roomDto;
    private Date entryDate; // 생성일(입장시간)
    private int currentScore; // 점수

    public ParticipantDto(Participant participant) {
        this.id = participant.getId();
        this.sessionId = participant.getSessionId();
        this.name = participant.getName();
        this.nickname = participant.getNickname();
        this.roomDto = new RoomDto(participant.getRoom());
        this.entryDate = participant.getEntryDate();
        this.currentScore = participant.getCurrentScore();
    }
}
