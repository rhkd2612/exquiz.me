package com.mumomu.exquizme.distribution.web.dto;

import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.domain.RoomState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Data @Builder
@AllArgsConstructor
public class RoomDto {
    private long pin;
    private int maxParticipantCount; // 최대 참여자 수
    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private RoomState currentState; // READY, PLAY, FINISH
    private int currentProblemNum; // 최근 진행중인 문제번호

    public RoomDto(Room room) {
        this.pin = room.getPin();
        this.maxParticipantCount = room.getMaxParticipantCount();
        this.startDate = room.getStartDate();
        this.endDate = room.getEndDate();
        this.currentState = room.getCurrentState();
        this.currentProblemNum = room.getCurrentProblemNum();
    }
}
