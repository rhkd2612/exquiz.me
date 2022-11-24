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
    private int beforeScore;
    private int imageNumber;
    private int colorNumber;
    private int totalCorrect;
    private int continuousCorrect; // 연속 정답
    private int continuousFailure; // 연속 실패
    private boolean isCorrect;

    public ParticipantDto(Participant participant) {
        setDefaultInfo(participant);
    }

    public ParticipantDto(Participant participant, boolean isCorrect) {
        setDefaultInfo(participant);
        this.isCorrect = isCorrect;
    }

    private void setDefaultInfo(Participant participant) {
        this.id = participant.getId();
        this.sessionId = participant.getSessionId();
        this.name = participant.getName();
        this.nickname = participant.getNickname();
        this.entryDate = participant.getEntryDate();
        this.beforeScore = participant.getBeforeScore();
        this.currentScore = participant.getCurrentScore();
        this.imageNumber = participant.getImageNumber();
        this.colorNumber = participant.getColorNumber();
        this.totalCorrect = participant.getTotalCorrect();
        this.continuousCorrect = participant.getContinuousCorrect();
        this.continuousFailure = participant.getContinuousFailure();
    }

}
