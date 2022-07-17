package com.mumomu.exquizme.distribution.domain;

import com.mumomu.exquizme.distribution.domain.key.ParticipantSessionKey;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity @Getter @Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ParticipantSessionKey.class)
public class ParticipantSession {
    @Id
    private Long participant_id;
    @Id
    private Long room_id;
    // 추후 제작(정보 부족)
}
