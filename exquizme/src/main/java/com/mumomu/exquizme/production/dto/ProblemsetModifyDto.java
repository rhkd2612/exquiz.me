package com.mumomu.exquizme.production.dto;

import lombok.Data;

@Data
public class ProblemsetModifyDto {
    private Long problemsetId;
    private String title;
    private String description;
    private String closingMent;
}
