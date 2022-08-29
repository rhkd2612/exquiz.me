package com.mumomu.exquizme.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mumomu.exquizme.production.domain.Host;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="oauth_user")
@Getter
public class OAuth2Account extends TimeEntity{
    @Id
    @Column(name="oauth_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    @Column(nullable = false, length=30, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(length = 100)
    private String password;

    @Column(name="picture")
    private String picture;

    @Column(nullable = false, length = 50)
    private String email;

    @JsonIgnore
    @Column(name="activated")
    private boolean activated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 회원 정보 수정 - 비 소셜 로그인 시 사용
    public void modify(String nickname, String password){
        this.nickname = nickname;
        this.password = password;
    }

    // 소셜로그인 시 이미 등록된 회원이라면 수정날짜만 업데이트하고 기존 데이터는 그대로 보존하도록 예외처리
    public OAuth2Account updateModifiedDate(){
        this.onPreUpdate();
        return this;
    }

    public String getRoleValue(){
        return this.role.getValue();
    }

    public boolean isActivated(){
        return activated;
    }
}
