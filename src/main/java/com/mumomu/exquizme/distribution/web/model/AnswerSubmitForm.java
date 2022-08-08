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
public class AnswerSubmitForm {
    @ApiModelProperty(value = "사용자 uuid", example = "asdkasdni214i21io42189dh210db")
    private String uuid;

    @ApiModelProperty(value = "제출 문제 번호", example = "1")
    private int problemIdx;

    @ApiModelProperty(value = "문제 정답", example = "1")
    private String answerText;
}
