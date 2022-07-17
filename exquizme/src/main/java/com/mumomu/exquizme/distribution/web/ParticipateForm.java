package com.mumomu.exquizme.distribution.web;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class ParticipateForm {
    // TODO 비속어 필터 필요
    private String name;
    private String nickname;
}
