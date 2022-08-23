package com.mumomu.exquizme.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO 권한 계층을 사용하면 좋을듯?
@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    SOCIAL("ROLE_SOCIAL");

    private final String value;
}
