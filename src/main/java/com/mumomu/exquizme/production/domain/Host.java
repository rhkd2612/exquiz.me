package com.mumomu.exquizme.production.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "host")
@Builder @Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Host {
    @Id
    @Column(name = "host_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore @Builder.Default
    @OneToMany(mappedBy = "host")
    private List<Problemset> problemsets = new ArrayList<>(); //자신의 문제 목록

    private String name;
    private String phoneNumber; //000-0000-0000
    private String nickname;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Boolean deleted;

    private String picture;
    private Character sex; //'M', 'F'
    private String email;
}
