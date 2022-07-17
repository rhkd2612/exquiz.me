package com.mumomu.exquizme.production.domain;

import com.mumomu.exquizme.distribution.domain.Room;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "problemset")
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problemset {
    @Id
    @Column(name = "problemset_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ProblemSet 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    //Problem 목록
    @OneToMany(mappedBy = "problemset", fetch = FetchType.LAZY)
    private List<Problem> problems;

    //Room 목록
    @OneToMany(mappedBy = "problemset", fetch = FetchType.LAZY)
    private List<Room> rooms;

    //태그 목록
    @OneToMany(mappedBy = "problemset")
    private List<ProblemsetTag> problemsetTags = new ArrayList<>();

    //ProblemSet 제목
    @Column(name = "title")
    private String title;

    //ProblemSet 설명
    @Column(name = "description")
    private String description;

    //ProblemSet 종료 멘트
    @Column(name = "closing")
    private String closing;

    //삭제 여부(비활성화 여부)
    @Column(name = "deleted")
    private Boolean deleted;

    //총 참가자 수
    @Column(name = "total_participant")
    private String totalParticipant;

    //만든 날짜
    @Column(name = "created_at")
    private Date createdAt;

    //업데이트된 날짜
    @Column(name = "updated_at")
    private Date updatedAt;

    //삭제 일자
    @Column(name = "deleted_at")
    private Date deletedAt;
}
