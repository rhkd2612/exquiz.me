package com.mumomu.exquizme.distribution.web.dto.stomp;
import com.mumomu.exquizme.common.entity.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@NoArgsConstructor
public abstract class StompMessage {
    //@Enumerated(EnumType.STRING)
    //protected Role toWhom; // 어떤 역할에게 보내는 메시지인지
    @Enumerated(EnumType.STRING)
    protected MessageFlag flag; // 어떤 메시지인지 구분
    protected String fromSession; // 보내는 세션 아이디
    //protected String message; // 보내는 메세지

    public StompMessage(MessageFlag flag, String fromSession) {
        setFlagAndSession(flag, fromSession);
    }

    public void setFlagAndSession(MessageFlag flag, String fromSession){
        this.flag = flag;
        this.fromSession = fromSession;
    }
}
