package com.mumomu.exquizme.production.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProblemsetModifyDto {
    private Long problemsetId;
    private String title;
    private String description;
    private String closingMent;
}
