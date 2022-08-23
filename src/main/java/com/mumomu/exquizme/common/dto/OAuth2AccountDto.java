package com.mumomu.exquizme.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mumomu.exquizme.common.entity.Role;
import com.mumomu.exquizme.common.entity.OAuth2Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2AccountDto {
    @NotNull
    @Size(min=3, max=30)
    private String username;

    @NotNull
    @Size(min=3, max=30)
    private String nickname;

    @NotNull
    @Size(min=3, max=50)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String picture;

    private String accessToken;

    @NotNull
    @Size(min=3, max=50)
    private String email;

    @NotNull
    private Role role;

    public OAuth2AccountDto(OAuth2Account oAuth2Account) {
        this.username = oAuth2Account.getUsername();
        this.nickname = oAuth2Account.getNickname();
        this.password = oAuth2Account.getPassword();
        this.email = oAuth2Account.getEmail();
        this.role = oAuth2Account.getRole();
        this.picture = oAuth2Account.getPicture();
    }

    public static OAuth2AccountDto from(OAuth2Account oAuth2Account) {
        if(oAuth2Account == null) return null;

        return OAuth2AccountDto.builder()
                .username(oAuth2Account.getUsername())
                .nickname(oAuth2Account.getNickname())
                .password(oAuth2Account.getPassword())
                .email(oAuth2Account.getEmail())
                .picture(oAuth2Account.getPicture())
                .role(oAuth2Account.getRole())
                .build();
    }
}
