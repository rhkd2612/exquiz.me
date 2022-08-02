package com.mumomu.exquizme.production.dto;

import com.mumomu.exquizme.production.domain.Problem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDto {
    private Long id;
    private Integer index;

    public ProblemDto(Problem problem) {
        this.id = problem.getId();
        this.index = problem.getIndex();
    }
}
