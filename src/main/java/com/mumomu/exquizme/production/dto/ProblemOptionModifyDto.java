package com.mumomu.exquizme.production.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProblemOptionModifyDto {
    private Long problemOptionId;
    private Integer idx;
    private String description;
    private String picture;
}
