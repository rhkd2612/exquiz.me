package com.mumomu.exquizme.production.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "problem")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn @Getter @SuperBuilder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem {
    @Id
    @Column(name = "problem_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "problemset_id")
    @JsonIgnore
    protected Problemset problemset;

    @Column(insertable = false, updatable = false)
    protected String dtype;

    @Setter
    protected String title;
    @Setter
    protected String description;
    @Setter
    protected Integer timelimit;
    @Setter
    protected Integer score;
    @Setter
    protected String picture;

    @Setter
    protected String videoUrl;

    @Setter
    protected String answer;

    @Setter
    protected Integer idx; //ProblemSet에서 이 Problem이 몇 번째인지

    protected Integer totalTry; //문제를 시도한 사람 수
    protected Integer totalCorrect; //문제를 맞춘 사람 수

    protected Date createdAt;
    @Setter
    protected Date updatedAt;
    @Setter
    protected Date deletedAt;
    @Setter
    protected Boolean deleted;

    public void reset(){
        this.totalTry = 0;
        this.totalCorrect = 0;
    }
    public int solve(){
        this.totalTry++;
        this.totalCorrect++;
        return (int)((double)this.score * Math.pow(0.95f, totalCorrect - 1));
    }

    public void wrong(){
        this.totalTry++;
    }
}
