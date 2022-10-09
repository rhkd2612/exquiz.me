package com.mumomu.exquizme.production.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProblemsetSaveDto {
    private Long hostId;
    private String title;
    private String description;
    private String closingMent;
    private Integer scoreSetting;
    private Integer timeSetting;
    private Integer backgroundMusic;
}
