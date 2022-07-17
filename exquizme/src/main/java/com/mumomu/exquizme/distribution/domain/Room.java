package com.mumomu.exquizme.distribution.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.servlet.http.Cookie;
import java.util.UUID;

@Entity
public class Room {
    @Id @GeneratedValue
    @Column(name = "room_id")
    private Long id;

    public static Cookie setAnonymousCookie(){
        UUID uuid = UUID.randomUUID();
        Cookie anonymousCookie = new Cookie("anonymousCode", uuid.toString());
        anonymousCookie.setComment("사용자 구분 코드");
        anonymousCookie.setMaxAge(60 * 60 * 3);
        return anonymousCookie;
    }
}
