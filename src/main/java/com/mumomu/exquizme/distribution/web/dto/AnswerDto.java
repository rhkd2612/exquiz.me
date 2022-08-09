package com.mumomu.exquizme.distribution.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mumomu.exquizme.distribution.domain.Answer;
import com.mumomu.exquizme.distribution.domain.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int problemIdx;
    private String answerText;

    public AnswerDto(Answer answer) {
        this.problemIdx = answer.getProblemIdx();
        this.answerText = answer.getAnswerText();
    }
}
