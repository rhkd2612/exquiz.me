package com.mumomu.exquizme.distribution.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity @Getter @Builder
@AllArgsConstructor
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

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumns({
//            @JoinColumn(name = "participant_id"),
//            @JoinColumn(name = "room_id")
//    })
//    private ParticipantSession participantSession; // 복합키 설정

    private Date entryDate; // 생성일(입장시간)
    private int currentScore; // 점수

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
