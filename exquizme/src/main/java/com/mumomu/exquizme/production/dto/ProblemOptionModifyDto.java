package com.mumomu.exquizme.production.dto;

import lombok.Data;

@Data
public class ProblemOptionModifyDto {
    private Long problemOptionId;
    private Integer index;
    private String description;
    private String picture;
}
