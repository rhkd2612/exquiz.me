package com.mumomu.exquizme.distribution.web.dto.stomp;


import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class StompParticipantSignup extends StompMessage{
    private Long id;
    private String name; // 사용자 구분 이름
    private String nickname;
    private Date entryDate; // 생성일(입장시간)
    private int currentScore; // 점수

    private int imageNumber; // 사용자 이미지
    private int colorNumber; // 사용자 배경색

    private List<ParticipantDto> participantList;

    public StompParticipantSignup(MessageType messageType, String fromSession, Participant participant, List<ParticipantDto> participantList, int imageNumber, int colorNumber) {
        super(messageType, fromSession);
        this.id = participant.getId();
        this.name = participant.getName();
        this.nickname = participant.getNickname();
        this.entryDate = participant.getEntryDate();
        this.currentScore = participant.getCurrentScore();
        this.participantList = participantList;
        this.imageNumber = imageNumber;
        this.colorNumber = colorNumber;
    }

    public StompParticipantSignup(Long id, String name, String nickname, Date entryDate, int currentScore, List<ParticipantDto> participantList, int imageNumber, int colorNumber) {
        super(null,null);
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.entryDate = entryDate;
        this.currentScore = currentScore;
        this.participantList = participantList;
        this.imageNumber = imageNumber;
        this.colorNumber = colorNumber;
    }
}
