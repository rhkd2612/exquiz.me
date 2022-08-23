package com.mumomu.exquizme.common.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    SUPER_ADMIN("ROLE_SUPER_ADMIN","슈퍼 관리자"),
    ADMIN("ROLE_ADMIN","관리자"),
    USER("ROLE_CLIENT","출제자"),
    GUEST("ROLE_USER","참여자");

    private final String key;
    private final String title;
}
