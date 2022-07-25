package com.mumomu.exquizme.production.dto;

import com.mumomu.exquizme.production.domain.ProblemOption;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class ProblemOptionSaveDto {
    private Long problemId;
    private Integer index; //몇 번째 선택지인지
    private String description;
    private String picture;

    public ProblemOptionSaveDto(ProblemOption problemOption) {
        this.problemId = problemOption.getProblem().getId();
        this.index = problemOption.getIndex();
        this.description = problemOption.getDescription();
        this.picture = problemOption.getPicture();
    }
}
