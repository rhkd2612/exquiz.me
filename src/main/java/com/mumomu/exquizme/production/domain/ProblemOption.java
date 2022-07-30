package com.mumomu.exquizme.production.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "problem_option")
@Builder @Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemOption {
    @Id
    @Column(name = "problem_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    @JsonIgnore
    private Problem problem;

    @Setter
    private Integer index; //몇 번째 선택지인지

    @Setter
    private String description;
    @Setter
    private String picture;

    private Integer pickcount; //몇 번 골라졌는지(통계 제공용)
}
