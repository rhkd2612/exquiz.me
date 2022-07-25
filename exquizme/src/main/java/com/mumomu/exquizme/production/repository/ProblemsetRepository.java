package com.mumomu.exquizme.production.repository;

import com.mumomu.exquizme.production.domain.Host;
import com.mumomu.exquizme.production.domain.Problemset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemsetRepository extends JpaRepository<Problemset, Long> {
    List<Problemset> findAllById(Long problemsetId);
    List<Problemset> findAllByHost(Host hostId);
    Optional<Problemset> findOneById(Long problemsetId);
    //List<Problemset> findAllByHost
}
