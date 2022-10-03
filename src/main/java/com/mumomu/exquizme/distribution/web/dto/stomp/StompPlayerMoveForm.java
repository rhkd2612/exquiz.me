package com.mumomu.exquizme.distribution.web.dto.stomp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StompPlayerMoveForm extends StompMessage{
    private int problemIdx; // 문제 번호
    private int y;
    private int x;

    public StompPlayerMoveForm(MessageFlag flag, String fromSession, int problemIdx, int y, int x) {
        super(flag, fromSession);
        this.problemIdx = problemIdx;
        this.y = y;
        this.x = x;
    }

    public StompPlayerMoveForm(int problemIdx, int y, int x) {
        super(null,null);
        this.problemIdx = problemIdx;
        this.y = y;
        this.x = x;
    }
}
