package com.mumomu.exquizme.production.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "problem_option")
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemOption {
    @Id
    @Column(name = "problem_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private Integer optionNumber; //몇 번째 선택지인지

    private String description;
    private String picture;

    private Integer pickcount; //몇 번 골라졌는지(통계 제공용)
}
