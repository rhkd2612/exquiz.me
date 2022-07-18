package com.mumomu.exquizme.distribution.domain;

import lombok.*;

import javax.persistence.*;

@Entity @Getter @Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantSession {
    @Id
    @Column(name="participant_id", nullable = false)
    private Long id;

    // @MapsId는 @Id로 지정한 컬럼에 @OneToOne, @ManyToOne 관계를 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId @JoinColumn(name="participant_id")
    private Participant participant;
    // 추후 제작(정보 부족)
}
