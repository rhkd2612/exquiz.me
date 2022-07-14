package com.mumomu.exquizme.production.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "problemset_tag")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemsetTag {
    @Id
    @Column(name = "problemset_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problemset_id")
    private Problemset problemset;

    @Column(name = "tag_name")
    private String tagName;
}
