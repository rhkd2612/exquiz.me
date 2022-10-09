package com.mumomu.exquizme.distribution.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mumomu.exquizme.distribution.exception.*;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.Problemset;
import lombok.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.*;

@Entity @Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id @GeneratedValue
    @Column(name = "room_id")
    private Long id;

    private String pin;
    private int maxParticipantCount; // 최대 참여자 수
    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private RoomState currentState; // READY, PLAY, FINISH
    private int currentProblemNum; // 최근 진행중인 문제번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="problemset_id")
    private Problemset problemset;

    @JsonIgnore
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();

    @Builder(builderClassName = "ByBasicBuilder", builderMethodName = "ByBasicBuilder")
    public Room(String pin, int maxParticipantCount, Problemset problemset){
        this.pin = pin;
        this.maxParticipantCount = maxParticipantCount;
        this.problemset = problemset;
        this.startDate = new Date();
        this.currentState = RoomState.READY;
        this.currentProblemNum = -1;
        this.participants = new ArrayList<>();
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCurrentState(RoomState currentState) {
        this.currentState = currentState;
    }

    public void setCurrentProblemNum(int currentProblemNum) {
        this.currentProblemNum = currentProblemNum;
    }
}
