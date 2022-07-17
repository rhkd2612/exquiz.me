package com.mumomu.exquizme.distribution.domain;

import com.mumomu.exquizme.distribution.domain.key.ParticipantSessionKey;
import lombok.*;

import javax.persistence.*;

@Entity @Getter @Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ParticipantSessionKey.class)
public class ParticipantSession {
    @Id
    @Column(name="participant_id")
    private Long participant_id;
    @Id
    @Column(name="room_id")
    private Long room_id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", updatable = false, insertable = false)
    private Participant participant;
    // 추후 제작(정보 부족)

//    public ParticipantSession(Long participant_id, Long room_id) {
//        this.participant_id = participant_id;
//        this.room_id = room_id;
//    }
}
