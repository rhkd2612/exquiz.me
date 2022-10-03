package com.mumomu.exquizme.distribution.web.dto.stomp;


import com.mumomu.exquizme.distribution.domain.Participant;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class StompParticipantSignup extends StompMessage{
    private Long id;
    private String name; // 사용자 구분 이름
    private String nickname;
    private Date entryDate; // 생성일(입장시간)
    private int currentScore; // 점수

    public StompParticipantSignup(MessageFlag flag, String fromSession, Participant participant) {
        super(flag, fromSession);
        this.id = participant.getId();
        this.name = participant.getName();
        this.nickname = participant.getNickname();
        this.entryDate = participant.getEntryDate();
        this.currentScore = participant.getCurrentScore();
    }

    public StompParticipantSignup(Long id, String name, String nickname, Date entryDate, int currentScore) {
        super(null,null);
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.entryDate = entryDate;
        this.currentScore = currentScore;
    }
}
