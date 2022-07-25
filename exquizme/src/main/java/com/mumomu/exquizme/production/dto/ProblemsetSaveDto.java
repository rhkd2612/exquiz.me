package com.mumomu.exquizme.production.dto;

import lombok.Data;

@Data
public class ProblemsetSaveDto {
    private Long hostId;
    private String title;
    private String description;
    private String closingMent;
}
