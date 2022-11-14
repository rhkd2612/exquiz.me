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
    private Integer idx; //몇 번째 선택지인지
    private String description;
    private String picture;
    private Integer pickCount;

    public ProblemOptionDto(ProblemOption problemOption) {
        this.id = problemOption.getId();
        this.idx = problemOption.getIdx();
        this.description = problemOption.getDescription();
        this.picture = problemOption.getPicture();
        this.pickCount = problemOption.getPickcount();
    }
}
