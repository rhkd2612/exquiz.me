package com.mumomu.exquizme.production.repository.problemtype;

import com.mumomu.exquizme.production.domain.problemtype.SubjectiveProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectiveProblemRepository extends JpaRepository<SubjectiveProblem, Long> {
    SubjectiveProblem findOneById(Long problemId);
}
