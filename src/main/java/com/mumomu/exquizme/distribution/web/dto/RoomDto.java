package com.mumomu.exquizme.distribution.web.dto;

import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.domain.RoomState;
import com.mumomu.exquizme.production.dto.ProblemsetDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Data @Builder
@AllArgsConstructor
@ApiModel(value="방 정보(DTO)", description = "개설된 방의 정보를 제공한다")
public class RoomDto {
    @ApiModelProperty(value="아이디")
    private Long id;
    @ApiModelProperty(value="핀번호")
    private String pin;
    @ApiModelProperty(value="최대 참여자 수")
    private int maxParticipantCount; // 최대 참여자 수
    @ApiModelProperty(value="개설일")
    private Date startDate;
    @ApiModelProperty(value="종료일")
    private Date endDate;

    @ApiModelProperty(value="문제셋Dto")
    private ProblemsetDto problemsetDto;

    @ApiModelProperty(value="방 상태(READY, PLAY, FINISH)")
    @Enumerated(EnumType.STRING)
    private RoomState currentState; // READY, PLAY, FINISH
    @ApiModelProperty(value="최근 진행중인 문제 번호")
    private int currentProblemNum; // 최근 진행중인 문제번호

    public RoomDto(Room room) {
        this.id = room.getId();
        this.pin = room.getPin();
        this.maxParticipantCount = room.getMaxParticipantCount();
        this.startDate = room.getStartDate();
        this.endDate = room.getEndDate();
        this.currentState = room.getCurrentState();
        this.currentProblemNum = room.getCurrentProblemNum();
        this.problemsetDto = new ProblemsetDto(room.getProblemset());
    }
}
