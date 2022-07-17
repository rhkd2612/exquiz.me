package com.mumomu.exquizme.distribution.domain.key;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
public class RoomResultKey implements Serializable {
    private Long participant_id;
    private Long room_id;
    private Long problem_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomResultKey that = (RoomResultKey) o;
        return Objects.equals(participant_id, that.participant_id) && Objects.equals(room_id, that.room_id) && Objects.equals(problem_id, that.problem_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participant_id, room_id, problem_id);
    }
}
