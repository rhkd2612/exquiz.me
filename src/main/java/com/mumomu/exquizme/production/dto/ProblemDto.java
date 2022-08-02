package com.mumomu.exquizme.production.dto;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.problemtype.MultipleChoiceProblem;
import com.mumomu.exquizme.production.domain.problemtype.OXProblem;
import com.mumomu.exquizme.production.domain.problemtype.SubjectiveProblem;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDto {
    private Long id;
    private String title;
    private String description;
    private Integer timelimit;
    private Integer score;
    private String picture;
    private String answer;
    private Integer index;

    public ProblemDto(Problem problem) {
        this.id = problem.getId();
        this.title = problem.getTitle();
        this.description = problem.getDescription();
        this.timelimit = problem.getTimelimit();
        this.score = problem.getScore();
        this.picture = problem.getPicture();
        this.answer = problem.getAnswer();
        this.index = problem.getIndex();
    }
}
