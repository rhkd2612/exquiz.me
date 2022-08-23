package com.mumomu.exquizme.common.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;

@Slf4j
public class TokenProvider {
    protected static final String AUTHORITIES_KEY = "auth";
    protected final String secret;
    protected final long tokenValidityInMilliseconds;

    protected Key key;

    public TokenProvider(String secret, long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;

        //시크릿 값을 decode해서 키 변수에 할당
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
}
