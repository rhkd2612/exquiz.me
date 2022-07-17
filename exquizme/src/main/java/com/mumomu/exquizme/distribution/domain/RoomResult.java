package com.mumomu.exquizme.distribution.domain;

import com.mumomu.exquizme.distribution.domain.key.ParticipantSessionKey;
import com.mumomu.exquizme.distribution.domain.key.RoomResultKey;
import lombok.*;

import javax.persistence.*;

@Entity @Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(RoomResultKey.class)
public class RoomResult {
    @Id
    private Long participant_id;
    @Id
    private Long room_id;
    @Id
    private Long problem_id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id", updatable = false, insertable = false)
    private Room room;
}
