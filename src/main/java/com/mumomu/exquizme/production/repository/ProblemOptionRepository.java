package com.mumomu.exquizme.production.repository;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemOptionRepository extends JpaRepository<ProblemOption, Long> {
    List<ProblemOption> findAllByProblemOrderByIdxAsc(Problem problem);

    Optional<ProblemOption> findOneById(Long problemOptionId);
}
