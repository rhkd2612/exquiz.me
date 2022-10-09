package com.mumomu.exquizme.production.domain.problemtype;

import com.mumomu.exquizme.production.domain.Problem;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Date;

@Entity @Getter @SuperBuilder
@DiscriminatorValue("SubjectiveProblem") @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubjectiveProblem extends Problem {
}
