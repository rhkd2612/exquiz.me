package com.mumomu.exquizme.distribution.domain;

import lombok.*;

import javax.persistence.*;

@Entity @Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomResult {
    @Id
    @Column(name="room_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId @JoinColumn(name="room_id")
    private Room room;
}
