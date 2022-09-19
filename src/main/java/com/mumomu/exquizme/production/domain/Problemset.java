package com.mumomu.exquizme.production.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mumomu.exquizme.distribution.domain.Room;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "problemset")
@Builder @Getter @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problemset {
    @Id
    @Column(name = "problemset_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    @Builder.Default
    @OneToMany(mappedBy = "problemset")
    private List<Problem> problems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "problemset")
    private List<Room> rooms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "problemset")
    private List<ProblemsetTag> problemsetTags = new ArrayList<>();

    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private String closingMent;

    @Setter
    private Integer scoreSetting;

    @Setter
    private Integer timeSetting;

    @Setter
    private Integer backgroundMusic;

    private Date createdAt;
    @Setter
    private Date updatedAt;
    @Setter
    private Date deletedAt;
    @Setter
    private Boolean deleted;

    private Integer totalParticipant;
}
