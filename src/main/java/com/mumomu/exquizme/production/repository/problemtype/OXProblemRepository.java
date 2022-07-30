package com.mumomu.exquizme.production.repository.problemtype;

import com.mumomu.exquizme.production.domain.problemtype.OXProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OXProblemRepository extends JpaRepository<OXProblem, Long> {
    OXProblem findOneById(Long problemId);
}
