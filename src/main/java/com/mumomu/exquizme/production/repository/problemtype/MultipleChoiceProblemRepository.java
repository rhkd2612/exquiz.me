package com.mumomu.exquizme.production.repository.problemtype;

import com.mumomu.exquizme.production.domain.problemtype.MultipleChoiceProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultipleChoiceProblemRepository extends JpaRepository<MultipleChoiceProblem, Long> {
    MultipleChoiceProblem findOneById(Long problemId);
}
