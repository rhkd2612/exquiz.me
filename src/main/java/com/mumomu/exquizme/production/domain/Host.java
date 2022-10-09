package com.mumomu.exquizme.production.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mumomu.exquizme.common.entity.OAuth2Account;
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

    @OneToOne(mappedBy = "host", fetch = FetchType.LAZY)
    private OAuth2Account oAuth2Account;

    @JsonIgnore @Builder.Default
    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<Problemset> problemsets = new ArrayList<>(); //자신의 문제 목록

    // TODO 사용자와 관련있는 것들(문제셋 제외)는 전부 OAuth2Account로 보내도 될듯?
    private String name;
    private String phoneNumber; //000-0000-0000
    private String nickname;

    // TODO OAuth2Account에 TimeEntity 상속으로 만들었으니 추후 제거
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Boolean deleted;

    private String picture;
    private Character sex; //'M', 'F'
    private String email;
}
