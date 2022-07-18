package com.mumomu.exquizme.distribution.web.dto;

import com.mumomu.exquizme.distribution.domain.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder @AllArgsConstructor
public class ParticipantDto {
    private String uuid;
    private String name; // 사용자 구분 이름
    private String nickname;
    private Date entryDate; // 생성일(입장시간)
    private int currentScore; // 점수

    public ParticipantDto(Participant participant) {
        this.uuid = participant.getUuid();
        this.name = participant.getName();
        this.nickname = participant.getNickname();
        this.entryDate = participant.getEntryDate();
        this.currentScore = participant.getCurrentScore();
    }
}
