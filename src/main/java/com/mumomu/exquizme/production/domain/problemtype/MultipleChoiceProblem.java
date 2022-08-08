package com.mumomu.exquizme.production.domain.problemtype;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import com.mumomu.exquizme.production.domain.Problemset;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @Getter @SuperBuilder
@DiscriminatorValue("MultipleChoiceProblem") @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MultipleChoiceProblem extends Problem {
    @OneToMany(mappedBy = "problem") @Builder.Default
    private List<ProblemOption> problemOptions = new ArrayList<>();
}
