package com.mumomu.exquizme.distribution.web.dto;

import com.mumomu.exquizme.distribution.domain.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder @AllArgsConstructor
public class ParticipantLeaderboardDto {
    private Long id;
    private String uuid;
    private String name; // 사용자 구분 이름
    private String nickname;
    private Date entryDate; // 생성일(입장시간)
    private int beforeScore; // 이전 점수
    private int currentScore; // 점수
    private int continuousCorrect; // 연속 정답
    private int continuousFailure; // 연속 실패

    public ParticipantLeaderboardDto(Participant participant) {
        this.id = participant.getId();
        this.uuid = participant.getUuid();
        this.name = participant.getName();
        this.nickname = participant.getNickname();
        this.entryDate = participant.getEntryDate();
        this.currentScore = participant.getCurrentScore();
        this.beforeScore = participant.getBeforeScore();
        this.continuousCorrect = participant.getContinuousCorrect();
        this.continuousFailure = participant.getContinuousFailure();
    }
}
