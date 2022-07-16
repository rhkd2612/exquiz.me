package com.mumomu.exquizme.production.domain.problemtype;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("OXProblem")
public class OXProblem extends Problem {
    @OneToMany(mappedBy = "problem")
    private List<ProblemOption> problemOptions = new ArrayList<>();

    //몇 번째 선택지가 답인지
    private Integer answer;
}
