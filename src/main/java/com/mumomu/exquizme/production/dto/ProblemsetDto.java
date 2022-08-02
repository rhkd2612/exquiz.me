package com.mumomu.exquizme.production.dto;

import com.mumomu.exquizme.production.domain.Problemset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemsetDto {
    private Long id;
    private String title;
    private String description;
    private String closingMent;

    public ProblemsetDto(Problemset problemset) {
        // TODO 추후에 변경하여야 한다
        if(problemset != null) {
            this.id = problemset.getId();
            this.title = problemset.getTitle();
            this.description = problemset.getDescription();
            this.closingMent = problemset.getClosingMent();
        }
    }
}
