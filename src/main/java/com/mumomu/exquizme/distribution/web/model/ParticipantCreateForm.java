package com.mumomu.exquizme.distribution.web.model;

import com.mumomu.exquizme.distribution.domain.Participant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantCreateForm {
    // TODO 비속어 필터 필요
    @ApiModelProperty(value = "사용자 인식 이름", example = "홍길동")
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "사용자 닉네임", example = "준현공듀")
    @NotEmpty
    private String nickname;

    public ParticipantCreateForm(Participant participant) {
        this.name = participant.getName();
        this.nickname = participant.getNickname();
    }
}
