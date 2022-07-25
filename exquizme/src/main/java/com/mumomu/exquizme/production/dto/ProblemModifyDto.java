package com.mumomu.exquizme.production.dto;

import lombok.Data;

@Data
public class ProblemModifyDto {
    private Long problemId;
    private String dtype;
    private Integer index;
    private String title;
    private String description;
    private Integer timelimit;
    private Integer score;
    private String picture;
    private String answer;
}
