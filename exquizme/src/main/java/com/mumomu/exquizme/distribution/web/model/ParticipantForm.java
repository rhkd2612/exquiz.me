package com.mumomu.exquizme.distribution.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ParticipantForm {
    // TODO 비속어 필터 필요
    @ApiModelProperty(value = "사용자 인식 이름", example = "홍길동")
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "사용자 닉네임", example = "준현공듀")
    @NotEmpty
    private String nickname;
}
