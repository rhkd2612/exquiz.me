package com.mumomu.exquizme.distribution.web.dto.stomp;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.dto.ProblemOptionDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StompNewProblemForm extends StompMessage{
    private Long id;
    private String title;
    private String description;
    private String dtype;
    private Integer timelimit;
    private Integer score;
    private String picture;
    private String videoUrl;
    private String answer;
    private Integer idx;
    private List<ProblemOptionDto> problemOptions;

    public StompNewProblemForm(MessageType messageType, String fromSession, Long id, String title, String description, String dtype, Integer timelimit, Integer score, String picture, String answer, Integer idx) {
        super(messageType, fromSession);
        this.id = id;
        this.title = title;
        this.description = description;
        this.dtype = dtype;
        this.timelimit = timelimit;
        this.score = score;
        this.picture = picture;
        this.answer = answer;
        this.idx = idx;
    }

    public StompNewProblemForm(MessageType messageType, String fromSession, Problem problem, List<ProblemOptionDto> problemOptions) {
        super(messageType, fromSession);
        this.id = problem.getId();
        this.title = problem.getTitle();
        this.description = problem.getDescription();
        this.dtype = problem.getDtype();
        this.timelimit = problem.getTimelimit();
        this.score = problem.getScore();
        this.picture = problem.getPicture();
        this.videoUrl = problem.getVideoUrl();
        this.answer = problem.getAnswer();
        this.idx = problem.getIdx();
        this.problemOptions = problemOptions;
    }
}
