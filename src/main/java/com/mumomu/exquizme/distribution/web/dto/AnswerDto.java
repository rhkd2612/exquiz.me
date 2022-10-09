package com.mumomu.exquizme.distribution.web.dto;

import com.mumomu.exquizme.distribution.domain.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int problemIdx;
    private String answerText;

    // @JsonDeserialize(using = LocalDateTimeDeserializer.class

    public AnswerDto(Answer answer) {
        this.problemIdx = answer.getProblemIdx();
        this.answerText = answer.getAnswerText();
    }
}
