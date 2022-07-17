package com.mumomu.exquizme.distribution.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Participant {
    @Id @GeneratedValue
    private Long id;

    private String uuid;

    private String name;
    private String nickname;


}
