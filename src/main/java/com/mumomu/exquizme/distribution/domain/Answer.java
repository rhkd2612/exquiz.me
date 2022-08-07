package com.mumomu.exquizme.distribution.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity @Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {
    @Id
    @GeneratedValue
    @Column(name="answer_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    private int problemIdx;
    private String answerText;

    @Builder(builderClassName = "ByBasicBuilder", builderMethodName = "ByBasicBuilder")
    public Answer(Participant participant, int problemIdx, String answerText) {
        this.participant = participant;
        this.problemIdx = problemIdx;
        this.answerText = answerText;
    }
}
