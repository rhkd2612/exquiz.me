package com.mumomu.exquizme.production.domain.problemtype;

import com.mumomu.exquizme.production.dto.ProblemOptionDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubjectiveProblemTest {
    @Test
    @DisplayName("영어 주관식 선지 생성")
    void randomEnglishSubjectProblemOptionTest(){
        String answer = "coffee";

        List<ProblemOptionDto> randomProblemOptions = SubjectiveProblem.createRandomProblemOptions(answer,15);

        for (ProblemOptionDto randomProblemOption : randomProblemOptions) {
            System.out.println(randomProblemOption.getDescription());
        }

        Assertions.assertThat(randomProblemOptions.size()).isEqualTo(15);
    }

    @Test
    @DisplayName("한글 주관식 선지 생성")
    void randomKoreanSubjectProblemOptionTest(){
        String answer = "인하대학교";

        List<ProblemOptionDto> randomProblemOptions = SubjectiveProblem.createRandomProblemOptions(answer,10);

        for (ProblemOptionDto randomProblemOption : randomProblemOptions) {
            System.out.println(randomProblemOption.getDescription());
        }

        Assertions.assertThat(randomProblemOptions.size()).isEqualTo(10);
    }
}