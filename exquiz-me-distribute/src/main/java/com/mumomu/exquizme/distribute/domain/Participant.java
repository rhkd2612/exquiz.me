package com.mumomu.exquizme.distribute.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.servlet.http.Cookie;
import java.util.UUID;

@Entity
@Getter @Setter
public class Participant {
    @Id @GeneratedValue
    private Long id;

    private String uuid;

    private String name;
    private String nickname;


}
