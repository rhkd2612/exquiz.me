package com.mumomu.exquizme.distribution.web.dto.stomp;

import com.mumomu.exquizme.common.entity.Role;
import lombok.Data;
import lombok.Getter;

@Getter
public class StompAnswerSubmitForm extends StompMessage{
    private int problemIdx; // 문제 번호
    private String answerText; // 문제 정답

    public StompAnswerSubmitForm(Role toWhom, String fromSession, String message, int problemIdx, String answerText) {
        super(toWhom, fromSession, message);
        this.problemIdx = problemIdx;
        this.answerText = answerText;
    }
}
