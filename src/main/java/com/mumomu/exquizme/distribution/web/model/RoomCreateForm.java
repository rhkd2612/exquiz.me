package com.mumomu.exquizme.distribution.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreateForm {
    @ApiModelProperty(value = "최대 참가자 수", example = "5")
    private int maxParticipantCount;
    @ApiModelProperty(value = "문제셋 ID", example = "1")
    private Long problemsetId;
    @ApiModelProperty(value = "퀴즈방 이름", example = "방입니다.")
    private String roomName;
}
