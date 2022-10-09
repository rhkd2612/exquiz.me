package com.mumomu.exquizme.production.repository;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.Problemset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findAllById(Long problemId);
    List<Problem> findAllByProblemsetAndDeletedOrderByIdxAsc(Problemset problemset, Boolean deleted);
    Optional<Problem> findOneById(Long problemId);
}
