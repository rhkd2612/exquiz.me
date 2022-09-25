package com.mumomu.exquizme.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO 권한 계층을 사용하면 좋을듯?
@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"), // 관리자
    USER("ROLE_USER"), // 출제자
    USER_SOCIAL("ROLE_USER_SOCIAL"), // 출제자 + 참여자 (for stomp messaging)
    SOCIAL("ROLE_SOCIAL"); // 참여자

    private final String value;
}
