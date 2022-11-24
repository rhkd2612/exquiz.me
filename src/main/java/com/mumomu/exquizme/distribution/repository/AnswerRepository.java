package com.mumomu.exquizme.distribution.repository;

import com.mumomu.exquizme.distribution.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
