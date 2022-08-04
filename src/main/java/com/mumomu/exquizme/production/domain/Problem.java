package com.mumomu.exquizme.production.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

@Entity
@Table(name = "problem")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn @Getter @SuperBuilder
@AllArgsConstructor @NoArgsConstructor
public class Problem {
    @Id
    @Column(name = "problem_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problemset_id")
    @JsonIgnore
    private Problemset problemset;

    @Column(insertable = false, updatable = false)
    private String dtype;

    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private Integer timelimit;
    @Setter
    private Integer score;
    @Setter
    private String picture;
    @Setter
    private String answer;

    @Setter
    private Integer idx; //ProblemSet에서 이 Problem이 몇 번째인지

    private Integer totalTry; //문제를 시도한 사람 수
    private Integer totalCorrect; //문제를 맞춘 사람 수

    private Date createdAt;
    @Setter
    private Date updatedAt;
    private Date deletedAt;
    private Boolean deleted;

    @Transactional
    public int solve(){
        this.totalTry++;
        this.totalCorrect++;
        return this.score;
    }

    @Transactional
    public void wrong(){
        this.totalTry++;
    }
}
