package com.mumomu.exquizme.production;

import com.mumomu.exquizme.production.domain.Host;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.domain.problemtype.MultipleChoiceProblem;
import com.mumomu.exquizme.production.dto.ProblemOptionSaveDto;
import com.mumomu.exquizme.production.dto.ProblemSaveDto;
import com.mumomu.exquizme.production.repository.ProblemRepository;
import com.mumomu.exquizme.production.repository.problemtype.MultipleChoiceProblemRepository;
import com.mumomu.exquizme.production.service.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@SpringBootTest
public class ProblemCRUDTest {
    @Autowired
    private EntityManager em;

    //ProblemService problemService;
    ProblemRepository problemRepository;
    MultipleChoiceProblemRepository multipleChoiceProblemRepository;

    @Test
    @Transactional
    void ProblemTest() {

        /*
        Host host = Host.builder()
                .build();
        Problemset problemset = Problemset.builder()
                .host(host)
                .title("2022년 7월 18일 쪽지시험")
                .build();

        MultipleChoiceProblem multipleChoiceProblem = MultipleChoiceProblem.builder()
                .problemset(problemset)
                .dtype("MultipleChoiceProblem")
                .build();

        em.persist(host);
        em.persist(problemset);
        em.persist(multipleChoiceProblem);

        System.out.println(multipleChoiceProblemRepository.findAll());

         */
    }
}
