package com.mumomu.exquizme.distribution.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import lombok.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @Getter @Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant {
    @Id @GeneratedValue
    @Column(name="participant_id")
    private Long id;
    private String uuid;

    private String name; // 사용자 구분 이름
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id")
    private Room room; // 입장 방


    @OneToMany(mappedBy = "participant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Answer> answers;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumns({
//            @JoinColumn(name = "participant_id"),
//            @JoinColumn(name = "room_id")
//    })
//    private ParticipantSession participantSession; // 복합키 설정

    private Date entryDate; // 생성일(입장시간)
    private int currentScore; // 점수
    private int beforeScore; // 이전 점수
    private int continuousCorrect; // 연속 정답
    private int continuousFailure; // 연속 실패

    @Builder(builderClassName = "ByBasicBuilder", builderMethodName = "ByBasicBuilder")
    public Participant(String name, String nickname, String uuid, Room room){
        this.name = name;
        this.nickname = nickname;
        this.uuid = uuid;
        this.room = room;
        this.entryDate = new Date();
        this.currentScore = 0;
        this.beforeScore = 0;
        this.continuousCorrect = 0;
        this.continuousFailure = 0;
        this.answers = new ArrayList<>();
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Transactional
    public int updateParticipantInfo(int score){
        beforeScore = currentScore;
        currentScore = currentScore + score;

        if(score > 0){
            continuousCorrect++;
            continuousFailure = 0;
        }else{
            continuousCorrect = 0;
            continuousFailure++;
        }

        return currentScore;
    }
}
