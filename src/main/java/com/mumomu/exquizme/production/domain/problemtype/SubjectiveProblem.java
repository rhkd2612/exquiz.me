package com.mumomu.exquizme.production.domain.problemtype;

import com.mumomu.exquizme.production.domain.Problem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity @Getter @SuperBuilder @NoArgsConstructor
@DiscriminatorValue("SubjectiveProblem")
public class SubjectiveProblem extends Problem {
}
