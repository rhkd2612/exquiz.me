package com.mumomu.exquizme.production.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "problem_option")
@Builder @Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProblemOption {
    @Id
    @Column(name = "problem_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "problem_id")
    @JsonIgnore
    private Problem problem;

    @Setter
    private Integer idx; //몇 번째 선택지인지

    @Setter
    private String description;
    @Setter
    private String picture;

    private Integer pickcount; //몇 번 골라졌는지(통계 제공용)
}
