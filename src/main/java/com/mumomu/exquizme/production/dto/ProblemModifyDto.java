package com.mumomu.exquizme.production.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProblemModifyDto {
    private Long problemId;
    private String dtype;
    private Integer idx;
    private String title;
    private String description;
    private Integer timelimit;
    private Integer score;
    private String picture;
    private String answer;
}
