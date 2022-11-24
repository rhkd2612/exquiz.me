package com.mumomu.exquizme.production.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.RoomState;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.NoMoreProblemException;
import com.mumomu.exquizme.production.domain.Host;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.domain.problemtype.MultipleChoiceProblem;
import com.mumomu.exquizme.production.domain.problemtype.OXProblem;
import com.mumomu.exquizme.production.domain.problemtype.SubjectiveProblem;
import com.mumomu.exquizme.production.dto.ProblemOptionDto;
import com.mumomu.exquizme.production.exception.*;
import com.mumomu.exquizme.production.repository.HostRepository;
import com.mumomu.exquizme.production.repository.ProblemOptionRepository;
import com.mumomu.exquizme.production.repository.ProblemRepository;
import com.mumomu.exquizme.production.repository.ProblemsetRepository;
import com.mumomu.exquizme.production.repository.problemtype.MultipleChoiceProblemRepository;
import com.mumomu.exquizme.production.repository.problemtype.OXProblemRepository;
import com.mumomu.exquizme.production.repository.problemtype.SubjectiveProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemsetRepository problemsetRepository;
    private final ProblemRepository problemRepository;
    private final MultipleChoiceProblemRepository multipleChoiceProblemRepository;
    private final OXProblemRepository oxProblemRepository;
    private final SubjectiveProblemRepository subjectiveProblemRepository;
    private final ProblemOptionRepository problemOptionRepository;
    private final HostRepository hostRepository;

    @Transactional
    public Problemset makeProblemset(
            Long hostId, String title,
            String description, String closingMent,
            Integer scoreSetting, Integer timeSetting,
            Integer backgroundMusic) throws HostNotFoundException {

        Optional<Host> hostOptional = hostRepository.findOneById(hostId);
        if (hostOptional.isEmpty()) {
            throw new HostNotFoundException("Host not found");
        }
        Host host = hostOptional.get();

        Problemset problemset = Problemset.builder()
                .host(host)
                .title(title)
                .description(description)
                .closingMent(closingMent)
                .createdAt(new Date())
                .updatedAt(new Date())
                .deleted(false)
                .totalParticipant(0)
                .scoreSetting(scoreSetting)
                .timeSetting(timeSetting)
                .backgroundMusic(backgroundMusic)
                .build();

        problemsetRepository.save(problemset);

        return problemset;
    }

    @Transactional
    public Problem makeProblem(
            Long problemsetId, String dtype,
            Integer idx, String title,
            String description, Integer timelimit,
            Integer score, String picture,
            String answer) throws Exception {

        Optional<Problemset> optionalProblemset = problemsetRepository.findOneById(problemsetId);
        if (optionalProblemset.isEmpty()) {
            throw new ProblemsetNotFoundException("Problemset not found");
        }
        Problemset problemset = optionalProblemset.get();

        if (dtype.equals("MultipleChoiceProblem")) {
            MultipleChoiceProblem multipleChoiceProblem = MultipleChoiceProblem.builder()
                    .problemset(problemset)
                    .dtype(dtype)
                    .title(title)
                    .description(description)
                    .timelimit(timelimit)
                    .score(score)
                    .picture(picture)
                    .idx(idx)
                    .answer(answer)
                    .totalTry(0)
                    .totalCorrect(0)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .deleted(false)
                    .build();

            multipleChoiceProblemRepository.save(multipleChoiceProblem);

            problemsetRepository.findOneById(problemsetId).get().getProblems().add(multipleChoiceProblem);

            return multipleChoiceProblem;
        }
        else if (dtype.equals("OXProblem")) {
            OXProblem oxProblem = OXProblem.builder()
                    .problemset(problemset)
                    .dtype(dtype)
                    .title(title)
                    .description(description)
                    .timelimit(timelimit)
                    .score(score)
                    .picture(picture)
                    .idx(idx)
                    .answer(answer)
                    .totalTry(0)
                    .totalCorrect(0)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .deleted(false)
                    .build();

            //TODO : OX문제 만들 때 선택지 자동생성

            oxProblemRepository.save(oxProblem);

            problemsetRepository.findOneById(problemsetId).get().getProblems().add(oxProblem);

            return oxProblem;
        }
        else if (dtype.equals("SubjectiveProblem")) {
            SubjectiveProblem subjectiveProblem = SubjectiveProblem.builder()
                    .problemset(problemset)
                    .dtype(dtype)
                    .title(title)
                    .description(description)
                    .timelimit(timelimit)
                    .score(score)
                    .picture(picture)
                    .idx(idx)
                    .answer(answer)
                    .totalTry(0)
                    .totalCorrect(0)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .deleted(false)
                    .build();

            subjectiveProblemRepository.save(subjectiveProblem);

            problemsetRepository.findOneById(problemsetId).get().getProblems().add(subjectiveProblem);

            return subjectiveProblem;
        }
        else { //dtype error
            throw new DtypeException("dtype does not match any problem types");
        }
    }

    @Transactional
    public ProblemOption makeProblemOption(
            Long problemId, Integer idx,
            String description, String picture) throws Exception {

        ProblemOption problemOption;
        Problem problem;

        Optional<Problem> problemOptional = problemRepository.findOneById(problemId);
        if (problemOptional.isEmpty()) {
            throw new ProblemNotFoundException("Problem not found");
        }
        problem = problemOptional.get();

        problemOption = ProblemOption.builder()
                .problem(problem)
                .idx(idx)
                .description(description)
                .picture(picture)
                .pickcount(0)
                .build();

        if (problem.getDtype().equals("MultipleChoiceProblem")) {
            ((MultipleChoiceProblem) problem).getProblemOptions().add(problemOption);
        }
        else if (problem.getDtype().equals("OXProblem")) {
            ((OXProblem) problem).getProblemOptions().add(problemOption);
        }
        else if (problem.getDtype().equals("SubjectiveProblem")) {
            throw new ProblemOptionAccessToSubjectiveProblemException("Adding problem option in subjective problem is not accepted");
        }
        else {
            throw new DtypeException("dtype doesn't fit into any of problem types");
        }

        problemOptionRepository.save(problemOption);

        return problemOption;
    }

    @Transactional
    public List<Problemset> getProblemsetsByHostId(Long hostId) {
        try {
            List<Problemset> problemsets = problemsetRepository.findAllByHostAndDeleted(hostRepository.findOneById(hostId).get(), false);

            for (Problemset problemset : problemsets) {
                problemset.getProblems();
            }

            return problemsets;
        } catch (Exception e) {
            throw new HostNotFoundException("Host not found");
        }
    }

    @Transactional
    public Problemset getProblemsetById(Long problemsetId){
        Optional<Problemset> targetProblemset = problemsetRepository.findOneById(problemsetId);

        if(targetProblemset.isEmpty())
            throw new ProblemsetNotFoundException("존재하지 않는 문제셋입니다.");

        targetProblemset.get().getProblems();

        return targetProblemset.get();
    }

    @Transactional
    public List<Problem> getProblemsByProblemsetId(Long problemsetId) {
        try {
            List<Problem> problems = problemRepository.findAllByProblemsetAndDeletedOrderByIdxAsc(problemsetRepository.findOneById(problemsetId).get(), false);
            return problems;
        } catch (Exception e) {
            throw new ProblemsetNotFoundException("Problemset Not Found");
        }
    }

    @Transactional
    public List<ProblemOptionDto> getProblemOptionById(Long problemId) throws Exception {
        Optional<Problem> problemOptional = problemRepository.findOneById(problemId);
        if (problemOptional.isEmpty()) {
            throw new ProblemNotFoundException("Problem not found");
        }
        Problem problem = problemOptional.get();
        if (problem.getDtype().equals("SubjectiveProblem")) {
            return SubjectiveProblem.createRandomProblemOptions(problem.getAnswer(), 10);
        }

        try {
            return problemOptionRepository.findAllByProblemOrderByIdxAsc(problemRepository.findOneById(problemId).get()).stream().map(p -> new ProblemOptionDto(p)).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ProblemNotFoundException("Problem not found");
        }
    }

    @Transactional
    public Problemset updateProblemset(
            Long problemsetId, String title,
            String description, String closingMent,
            Integer scoreSetting, Integer timeSetting,
            Integer backgroundMusic) {
        Optional<Problemset> problemsetOptional = problemsetRepository.findOneById(problemsetId);
        if (problemsetOptional.isEmpty()) {
            throw new ProblemsetNotFoundException("Problemset not found");
        }

        Problemset problemset = problemsetOptional.get();
        problemset.setTitle(title);
        problemset.setDescription(description);
        problemset.setClosingMent(closingMent);
        problemset.setUpdatedAt(new Date());
        problemset.setScoreSetting(scoreSetting);
        problemset.setTimeSetting(timeSetting);
        problemset.setBackgroundMusic(backgroundMusic);

        problemsetRepository.save(problemset);

        return problemset;
    }

    @Transactional
    public Problem updateProblem(
            Long problemId, String dtype,
            Integer idx, String title,
            String description, Integer timelimit,
            Integer score, String picture,
            String answer) throws Exception {
        Optional<Problem> problemOptional = problemRepository.findOneById(problemId);
        if (problemOptional.isEmpty()) {
            throw new ProblemNotFoundException("Problem not found");
        }

        Problem problem = problemOptional.get();
        String curDtype = problem.getDtype();

        if (curDtype.equals(dtype)) { //문제 유형이 같을 경우
            if (curDtype.equals("MultipleChoiceProblem")) {
                MultipleChoiceProblem multipleChoiceProblem = multipleChoiceProblemRepository.findOneById(problemId);
                multipleChoiceProblem.setIdx(idx);
                multipleChoiceProblem.setTitle(title);
                multipleChoiceProblem.setDescription(description);
                multipleChoiceProblem.setTimelimit(timelimit);
                multipleChoiceProblem.setScore(score);
                multipleChoiceProblem.setPicture(picture);
                multipleChoiceProblem.setAnswer(answer);
                multipleChoiceProblem.setUpdatedAt(new Date());

                multipleChoiceProblemRepository.save(multipleChoiceProblem);

                return multipleChoiceProblem;
            }
            else if (curDtype.equals("OXProblem")) {
                OXProblem oxProblem = oxProblemRepository.findOneById(problemId);
                oxProblem.setIdx(idx);
                oxProblem.setTitle(title);
                oxProblem.setDescription(description);
                oxProblem.setTimelimit(timelimit);
                oxProblem.setScore(score);
                oxProblem.setPicture(picture);
                oxProblem.setAnswer(answer);
                oxProblem.setUpdatedAt(new Date());

                oxProblemRepository.save(oxProblem);

                return oxProblem;
            }
            else if (curDtype.equals("SubjectiveProblem")) {
                SubjectiveProblem subjectiveProblem = subjectiveProblemRepository.findOneById(problemId);
                subjectiveProblem.setIdx(idx);
                subjectiveProblem.setTitle(title);
                subjectiveProblem.setDescription(description);
                subjectiveProblem.setTimelimit(timelimit);
                subjectiveProblem.setScore(score);
                subjectiveProblem.setPicture(picture);
                subjectiveProblem.setAnswer(answer);
                subjectiveProblem.setUpdatedAt(new Date());


                subjectiveProblemRepository.save(subjectiveProblem);

                return subjectiveProblem;
            }
            else {
                throw new DtypeException("dtype doesn't match with any of problem types");
            }
        }
        else {
            throw new DifferentDtypeException("new dtype should be same with original problem dtype");
        }
    }

    @Transactional
    public ProblemOption updateProblemOption(
            Long problemOptionId, Integer idx,
            String description, String picture) {
        Optional<ProblemOption> problemOptionOptional = problemOptionRepository.findOneById(problemOptionId);
        if (problemOptionOptional.isEmpty()) {
            throw new ProblemOptionNotFoundException("Problem option not found");
        }

        ProblemOption problemOption = problemOptionOptional.get();

        problemOption.setIdx(idx);
        problemOption.setDescription(description);
        problemOption.setPicture(picture);

        problemOptionRepository.save(problemOption);

        return problemOption;
    }


    @Transactional
    public void deleteProblemset(Long problemsetId) throws ProblemsetNotFoundException {
        Optional<Problemset> problemsetOptional = problemsetRepository.findOneById(problemsetId);
        if (problemsetOptional.isEmpty()) {
            throw new ProblemsetNotFoundException("Problemset not found");
        }
        Problemset problemset = problemsetOptional.get();

        problemset.setDeleted(true);
        problemset.setDeletedAt(new Date());

        problemsetRepository.save(problemset);
    }

    @Transactional
    public void deleteProblem(Long problemId) throws ProblemNotFoundException {
        Optional<Problem> problemOptional = problemRepository.findOneById(problemId);
        if (problemOptional.isEmpty()) {
            throw new ProblemNotFoundException("Problem not found");
        }
        Problem problem = problemOptional.get();

        problem.setDeleted(true);
        problem.setDeletedAt(new Date());

        problemRepository.save(problem);
    }
}
