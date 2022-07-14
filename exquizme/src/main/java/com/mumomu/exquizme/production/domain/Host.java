package com.mumomu.exquizme.production.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "host")
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Host {
    @Id
    @Column(name = "host_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //자신의 문제 목록
    @OneToMany(mappedBy = "host")
    private List<Problemset> problemSets = new ArrayList<>();

    //이름
    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber; //000-0000-0000

    //닉네임
    @Column(name = "nickname")
    private String nickname;

    //가입일자
    @Column(name = "created_at")
    private Date createdAt;

    //탈퇴일자
    @Column(name = "updated_at")
    private Date updatedAt;

    //프로필 사진
    @Column(name = "picture")
    private String picture;

    //성별
    @Column(name = "sex")
    private Character sex; //'M', 'F'

    @Column(name = "email")
    private String email;

    //삭제 여부
    @Column(name = "deleted")
    private Boolean deleted;
}
