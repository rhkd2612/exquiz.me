package com.mumomu.exquizme.distribution.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mumomu.exquizme.distribution.exception.ClosedRoomAccessException;
import com.mumomu.exquizme.distribution.exception.InvalidParticipantAccessException;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.NoMoreProblemException;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.Problemset;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        this.currentProblemNum = 0;
        this.participants = new ArrayList<>();
    }

    public void addParticipant(Participant participant) {
        if(!this.participants.contains(participant))
            this.participants.add(participant);
    }

    public int currentParticipantsSize(){
        System.out.println("participants" + this.participants);
        if(this.participants == null || this.participants.isEmpty())
            return 0;
        return this.participants.size();
    }

    public static Cookie setAnonymousCookie(){
        UUID uuid = UUID.randomUUID();
        Cookie anonymousCookie = new Cookie("anonymousCode", uuid.toString());
        anonymousCookie.setComment("사용자 구분 코드");
        anonymousCookie.setMaxAge(60 * 60 * 3); // 쿠키 지속 시간
        return anonymousCookie;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

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

        this.participants = new ArrayList<>();
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
