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

    @Transactional
    public void addParticipant(Participant participant){
        this.participants.add(participant);
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Transactional
    public void setProblemset(Problemset problemset) {
        this.problemset = problemset;
    }

    public void setCurrentState(RoomState currentState) {
        this.currentState = currentState;
    }

    @Transactional
    public Problem startRoom() throws InvalidRoomAccessException {
        if(this.currentState != RoomState.READY)
            throw new InvalidRoomAccessException("해당하는 시작 대기 중인 방이 없습니다.");
        this.currentState = RoomState.PLAY;
        this.currentProblemNum = 0;
        return this.problemset.getProblems().get(this.currentProblemNum);
    }

    @Transactional
    public Problem nextProblem() throws NoMoreProblemException {
        List<Problem> problems = this.problemset.getProblems();

        System.out.println("nextProblem");
        System.out.println(problems.size());
        System.out.println(currentProblemNum);

        if(this.currentProblemNum + 1 >= problems.size())
            throw new NoMoreProblemException("문제셋에 남은 문제가 없습니다.");

        this.currentProblemNum++;
        return problems.get(this.currentProblemNum);
    }
}
