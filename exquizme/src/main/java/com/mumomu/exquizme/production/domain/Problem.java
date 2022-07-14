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

    //문제 제목
    @Column(name = "title")
    private String title;

    //문제 설명
    @Column(name = "description")
    private String description;

    //시간 제한(정수 단위)
    @Column(name = "timelimit")
    private Integer timelimit;

    //문제 배점
    @Column(name = "score")
    private Integer score;

    //문제 사진
    @Column(name = "picture")
    private String picture;

    //ProblemSet에서 이 Problem이 몇 번째인지
    @Column(name = "sequence")
    private Integer sequence;

    //문제를 시도한 사람 수
    @Column(name = "total_try")
    private Integer totalTry;

    //문제를 맞춘 사람 수
    @Column(name = "total_correct")
    private Integer totalCorrect;

    //삭제 여부
    @Column(name = "deleted")
    private Boolean deleted;

    //만든 날짜
    @Column(name = "created_at")
    private Date createdAt;

    //업데이트된 날짜
    @Column(name = "updated_at")
    private Date updatedAt;
}
