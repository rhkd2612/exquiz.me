package com.mumomu.exquizme.distribution.web.dto.stomp;

import com.mumomu.exquizme.production.domain.Problem;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String answer;
    private Integer idx;

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

    public StompNewProblemForm(MessageType messageType, String fromSession, Problem problem) {
        super(messageType, fromSession);
        this.id = problem.getId();
        this.title = problem.getTitle();
        this.description = problem.getDescription();
        this.dtype = problem.getDtype();
        this.timelimit = problem.getTimelimit();
        this.score = problem.getScore();
        this.picture = problem.getPicture();
        this.answer = problem.getAnswer();
        this.idx = problem.getIdx();
    }
}
