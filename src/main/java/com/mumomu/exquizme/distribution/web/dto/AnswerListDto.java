package com.mumomu.exquizme.distribution.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerListDto {
    private int totalCorrectCount;
    private List<ParticipantDto> beforeParticipantInfo = new ArrayList<>();
    private List<ParticipantDto> participantInfo = new ArrayList<>();
    private List<Boolean> isCorrect = new ArrayList<>();

    public void addParticipant(ParticipantDto participantDto, boolean isCorrect){
        beforeParticipantInfo.add(participantDto);
        participantInfo.add(participantDto);
        this.isCorrect.add(isCorrect);

        if(isCorrect)
            this.totalCorrectCount++;
    }

    public void sortParticipantByScore(){
        beforeParticipantInfo.sort(Comparator.comparing(ParticipantDto::getBeforeScore, Comparator.reverseOrder()));
        participantInfo.sort(Comparator.comparing(ParticipantDto::getCurrentScore, Comparator.reverseOrder()));
    }
}
