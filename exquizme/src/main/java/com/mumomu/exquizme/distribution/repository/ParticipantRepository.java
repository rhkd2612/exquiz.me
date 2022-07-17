package com.mumomu.exquizme.distribution.repository;

import com.mumomu.exquizme.distribution.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByUuid(String uuid);
}
