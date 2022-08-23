package com.mumomu.exquizme.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    SOCIAL("ROLE_SOCIAL");

    private final String value;
}
