package com.mumomu.exquizme.production.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "problem")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class Problem {
    @Id
    @Column(name = "problem_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problemset_id")
    private Problemset problemset;

    private String title;
    private String description;
    private Integer timelimit;
    private Integer score;
    private String picture;

    private Integer sequence; //ProblemSet에서 이 Problem이 몇 번째인지

    private Integer totalTry; //문제를 시도한 사람 수
    private Integer totalCorrect; //문제를 맞춘 사람 수

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Boolean deleted;
}
