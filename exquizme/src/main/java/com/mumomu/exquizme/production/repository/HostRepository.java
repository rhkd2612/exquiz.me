package com.mumomu.exquizme.production.repository;

import com.mumomu.exquizme.production.domain.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HostRepository extends JpaRepository<Host, Long> {
    List<Host> findAllById(Long hostId);
    Optional<Host> findOneById(Long hostId);
}
