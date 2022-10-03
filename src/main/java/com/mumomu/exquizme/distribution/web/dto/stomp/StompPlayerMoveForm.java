package com.mumomu.exquizme.distribution.web.dto.stomp;

import com.mumomu.exquizme.common.entity.Role;
import lombok.Getter;

@Getter
public class StompPlayerMoveForm extends StompMessage{
    private int problemIdx; // 문제 번호
    private int y;
    private int x;

    public StompPlayerMoveForm(Role toWhom, String fromSession, String message, int problemIdx, int y, int x) {
        super(toWhom, fromSession, message);
        this.problemIdx = problemIdx;
        this.y = y;
        this.x = x;
    }
}
