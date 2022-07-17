package com.mumomu.exquizme.distribution.domain.key;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter @Setter
public class ParticipantSessionKey implements Serializable {
    private Long participant_id;
    private Long room_id;
}
