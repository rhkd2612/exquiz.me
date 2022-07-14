package com.mumomu.exquizme.production.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "problem_option")
@Builder
@AllArgsConstructor @NoArgsConstructor
public class ProblemOption {
    @Id
    @Column(name = "problem_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    //몇 번째 선택지인지
    @Column(name = "option_number")
    private Integer optionNumber;

    //선택지 설명
    @Column(name = "description")
    private String description;

    //선택지 그림
    @Column(name = "picture")
    private String picture;

    //몇 번 골라졌는지(통계 제공용)
    @Column(name = "pickcount")
    private Integer pickcount;
}
