package com.mumomu.exquizme.distribution.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerListDto {
    private int totalCorrectCount;
    private List<ParticipantDto> participantInfo = new ArrayList<>();
    private List<Boolean> isCorrect = new ArrayList<>();

    public void addParticipant(ParticipantDto participantDto, boolean isCorrect){
        participantInfo.add(participantDto);
        this.isCorrect.add(isCorrect);

        if(isCorrect)
            this.totalCorrectCount++;
    }
}
