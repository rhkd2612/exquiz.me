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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    @OneToMany(mappedBy = "problemset", fetch = FetchType.LAZY)
    private List<Problem> problems;

    @OneToMany(mappedBy = "problemset", fetch = FetchType.LAZY)
    private List<Room> rooms;

    @OneToMany(mappedBy = "problemset")
    private List<ProblemsetTag> problemsetTags = new ArrayList<>();

    private String title;
    private String description;
    private String closingMent;

    private Boolean deleted;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private String totalParticipant;
}
