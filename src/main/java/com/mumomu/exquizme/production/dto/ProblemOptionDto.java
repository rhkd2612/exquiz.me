package com.mumomu.exquizme.production.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemOptionDto {
    private Long id;
    private Problem problem;
    private Integer index; //몇 번째 선택지인지
    private String description;
    private String picture;
    private Integer pickcount;

    public ProblemOptionDto(ProblemOption problemOption) {
        this.id = problemOption.getId();
        this.problem = problemOption.getProblem();
        this.index = problemOption.getIndex();
        this.description = problemOption.getDescription();
        this.picture = problemOption.getPicture();
        this.pickcount = problemOption.getPickcount();
    }
}
