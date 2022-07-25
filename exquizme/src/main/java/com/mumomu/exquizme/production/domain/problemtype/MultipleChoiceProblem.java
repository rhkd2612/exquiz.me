package com.mumomu.exquizme.production.domain.problemtype;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @SuperBuilder @NoArgsConstructor
@DiscriminatorValue("MultipleChoiceProblem")
public class MultipleChoiceProblem extends Problem {
    @OneToMany(mappedBy = "problem")
    private List<ProblemOption> problemOptions = new ArrayList<>();

    @Setter
    private Integer answer;
}
