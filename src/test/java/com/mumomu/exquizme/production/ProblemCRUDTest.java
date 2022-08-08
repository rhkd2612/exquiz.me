package com.mumomu.exquizme.production;

import com.mumomu.exquizme.production.domain.Host;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.domain.problemtype.MultipleChoiceProblem;
import com.mumomu.exquizme.production.domain.problemtype.OXProblem;
import com.mumomu.exquizme.production.domain.problemtype.SubjectiveProblem;
import com.mumomu.exquizme.production.repository.HostRepository;
import com.mumomu.exquizme.production.repository.ProblemOptionRepository;
import com.mumomu.exquizme.production.repository.ProblemRepository;
import com.mumomu.exquizme.production.repository.ProblemsetRepository;
import com.mumomu.exquizme.production.repository.problemtype.MultipleChoiceProblemRepository;
import com.mumomu.exquizme.production.repository.problemtype.OXProblemRepository;
import com.mumomu.exquizme.production.repository.problemtype.SubjectiveProblemRepository;
import com.mumomu.exquizme.production.service.ProblemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ProblemCRUDTest {
    @Autowired
    private EntityManager em;

    @Autowired
    HostRepository hostRepository;
    @Autowired
    ProblemsetRepository problemsetRepository;
    @Autowired
    ProblemRepository problemRepository;
    @Autowired
    MultipleChoiceProblemRepository multipleChoiceProblemRepository;
    @Autowired
    OXProblemRepository oxProblemRepository;
    @Autowired
    SubjectiveProblemRepository subjectiveProblemRepository;
    @Autowired
    ProblemOptionRepository problemOptionRepository;

    @Autowired
    ProblemService problemService;

    Host host;
    Problemset problemset;
    MultipleChoiceProblem multipleChoiceProblem;
    OXProblem oxProblem;
    SubjectiveProblem subjectiveProblem;

    @BeforeEach
    void initialize() throws Exception {
/*
        host = Host.builder()
                .id(1L)
                .name("이상빈")
                .nickname("Mumomu")
                .build();
*/
        problemset = problemService.makeProblemset(1L, "2022년 7월 25일 쪽지시험", "쪽지시험입니다.", "수고하셨습니다");
        multipleChoiceProblem = (MultipleChoiceProblem) problemService.makeProblem(problemset.getId(), "MultipleChoiceProblem",
                1, "가장 높은 산", "한국에서 가장 높은 산은?", 20, 100, null, "0");

        oxProblem = (OXProblem) problemService.makeProblem(problemset.getId(), "OXProblem", 2, "한국은 영어로?", "한국은 영어로 Korea다.",
                10, 50, null, "0");
        subjectiveProblem = (SubjectiveProblem) problemService.makeProblem(problemset.getId(), "SubjectiveProblem", 3, "뮤묘뮤",
                "뮤묘뮤의 팀장은?", 15, 200, null, "이상빈");

        problemService.makeProblemOption(multipleChoiceProblem.getId(), 3, "남한산", null);
        problemService.makeProblemOption(multipleChoiceProblem.getId(), 1, "지리산", null);
        problemService.makeProblemOption(multipleChoiceProblem.getId(), 0, "한라산", null);
        problemService.makeProblemOption(multipleChoiceProblem.getId(), 2, "북한산", null);

        problemService.makeProblemOption(oxProblem.getId(), 0, "O", null);
        problemService.makeProblemOption(oxProblem.getId(), 1, "X", null);

    }

    @Test
    @Transactional
    void getProblemsetTest() {
        /*
        List<Problemset> problemsets = problemService.getProblemset(1L);
        for (Problemset ps : problemsets) {
            System.out.println("title : " + ps.getTitle());
            System.out.println("description : " + ps.getDescription());
            System.out.println("closing : " + ps.getClosingMent());
        }
         */

        List<Problemset> problemsets2 = hostRepository.findOneById(1L).get().getProblemsets();
        for (Problemset ps : problemsets2) {
            System.out.println("title : " + ps.getTitle());
            System.out.println("description : " + ps.getDescription());
            System.out.println("closing : " + ps.getClosingMent());
        }
    }

    @Test
    @Transactional
    void postProblemsetTest() throws Exception {
        problemService.makeProblemset(1L, "Problemset Post 테스트 퀴즈", "테스트용 퀴즈입니다.",
                "클로징 멘트");
        getProblemsetTest();

        assertThrows(Exception.class, () -> { //존재하지 않는 Host에 Problemset 생성 시도
            problemService.makeProblemset(99999L, "hostid가 잘못된 퀴즈",
                "exception 반환해야 함", "클로징");
        });
    }

    @Test
    @Transactional
    void putProblemsetTest() throws Exception {
        problemService.updateProblemset(problemset.getId(), "타이틀 변경", "디스크립션 변경", "클로징멘트 변경");

        assertThrows(Exception.class, () -> { //존재하지 않는 Problemset 업데이트 시도
           problemService.updateProblemset(99999L, "타이틀 변경", "디스크립션 변경", "클로징멘트 변경");
        });

        getProblemsetTest();
    }

    @Test
    @Transactional
    void deleteProblemsetTest() throws Exception {
        problemService.deleteProblemset(problemService.getProblemset(1L).get(0).getId());

        assertThrows(Exception.class, () -> {
           problemService.deleteProblemset(99999L);
        });
    }

    @Test
    @Transactional
    void deleteProblemTest() throws Exception {
        problemService.deleteProblem(problemService.getProblems(problemset.getId()).get(0).getId());

        assertThrows(Exception.class, () -> {
            problemService.deleteProblem(99999L);
        });
    }

    @Test
    @Transactional
    void getProblemTest() {
        List<Problem> problems = problemService.getProblems(problemset.getId());
        for (Problem p : problems) {
            System.out.println("id : " + p.getId());
            System.out.println("title : " + p.getTitle());
            System.out.println("dtype : " + p.getDtype());
            System.out.println("description : " + p.getDescription());
            System.out.println("idx : " + p.getIdx() + "\n"); //idx 순으로 정렬되어 나옴
        }


        List<Problem> problems2 = problemsetRepository.findOneById(problemset.getId()).get().getProblems();
        System.out.println("size = " + problems2.size());
        for (Problem p : problems2) {
            System.out.println("id : " + p.getId());
            System.out.println("title : " + p.getTitle());
            System.out.println("dtype : " + p.getDtype());
            System.out.println("description : " + p.getDescription());
            System.out.println("idx : " + p.getIdx() + "\n"); //idx 순으로 정렬되어 나옴
        }
    }

    @Test
    @Transactional
    void postProblemTest() throws Exception {
        problemService.makeProblem(problemset.getId(), "MultipleChoiceProblem", 4,
                "테스트용 타이틀", "테스트용 지문", 40, 50, null, "1");

        assertThrows(Exception.class, () -> { //존재하지 않는 problemset에 post 시도
            problemService.makeProblem(99999L, "MultipleChoiceProblem", 4,
                    "테스트용 타이틀", "테스트용 지문", 40, 50, null, "1");
        });

        getProblemTest();
    }

    @Test
    @Transactional
    void putProblemTest() throws Exception {
        List<Problem> problems = problemService.getProblems(problemset.getId());
        for (int i = 0; i < problems.size(); i++) {
            problemService.updateProblem(problems.get(i).getId(), problems.get(i).getDtype(),
                    i + 1, "새로운 타이틀 #" + (i + 1), "새로운 지문 #" + (i + 1),
                    10 * (i + 1), 100 * (i + 1), null, problems.get(i).getAnswer());
        }
        assertThrows(Exception.class, () -> { //존재하지 않는 Problem 수정 시도
           problemService.updateProblem(99999L, "MultipleChoiceQuestion", 5,
                   "새로운 타이틀", "새로운 지문", 10, 100, null, "1");
        });

        /* TODO : dtype 다를 때 수정 구현 이후 테스트 예정
        assertThrows(Exception.class, () -> { //존재하지 않는 dtype으로 수정 시도
           problemService.updateProblem(99999L, "nosuchdtypeexists", 5,
                   "새로운 타이틀", "새로운 지문", 10, 100, null, "1");
        });
         */

        getProblemTest();
    }

    @Test
    @Transactional
    void getProblemOptionTest() throws Exception {
        List<ProblemOption> problemOptions;
        List<Problem> problems = problemService.getProblems(problemset.getId());
        for (int i = 0; i < problems.size(); i++) {
            if (problems.get(i).getDtype().equals("SubjectiveProblem")) {
                final int temp = i;
                assertThrows(Exception.class, () -> { //주관식 문제는 ProblemOption이 없다
                    problemService.getProblemOption(problems.get(temp).getId());
                });
                continue;
            }
            problemOptions = problemService.getProblemOption(problems.get(i).getId());
            for (ProblemOption po : problemOptions) {
                System.out.println("id : " + po.getId());
                System.out.println("idx : " + po.getIdx());
                System.out.println("description : " + po.getDescription() + "\n"); //idx 순으로 정렬되어 나옴
            }
        }

        for (int i = 0; i < problems.size(); i++) {
            if (problems.get(i).getDtype().equals("MultipleChoiceProblem")) {
                problemOptions = ((MultipleChoiceProblem) problems.get(i)).getProblemOptions();
                System.out.println("size = " + problemOptions.size());
                for (ProblemOption po : problemOptions) {
                    System.out.println("id : " + po.getId());
                    System.out.println("idx : " + po.getIdx());
                    System.out.println("description : " + po.getDescription() + "\n"); //idx 순으로 정렬되어 나옴
                }
            }
        }
    }

    @Test
    @Transactional
    void postProblemOptionTest() throws Exception {
        postProblemTest();

        List<Problem> problems = problemService.getProblems(problemset.getId());
        Problem curProblem = problems.get(problems.size() - 1);

        problemService.makeProblemOption(curProblem.getId(), 3, "새로 만든 선택지 4", null);
        problemService.makeProblemOption(curProblem.getId(), 1, "새로 만든 선택지 2", null);
        problemService.makeProblemOption(curProblem.getId(), 0, "새로 만든 선택지 1", null);
        problemService.makeProblemOption(curProblem.getId(), 2, "새로 만든 선택지 3", null);

        assertThrows(Exception.class, () -> { //존재하지 않는 problem에 선택지 추가 시도
            problemService.makeProblemOption(99999L, 0, "새로 만들려고 하는 선택지", null);
        });

        assertThrows(Exception.class, () -> { //주관식 문제에 선택지 추가 시도
            problemService.makeProblemOption(subjectiveProblem.getId(), 0, "새로 만들려고 하는 선택지", null);
        });

        getProblemOptionTest();
    }

    @Test
    @Transactional
    void putProblemOptionTest() throws Exception {
        List<ProblemOption> problemOptions = problemService.getProblemOption(multipleChoiceProblem.getId());
        for (int i = 0; i < problemOptions.size(); i++) {
            problemService.updateProblemOption(problemOptions.get(i).getId(), i, "업데이트하는 설명 #" + (i + 1), null);
        }

        assertThrows(Exception.class, () -> { //존재하지 않는 problem option에 수정 시도
            problemService.updateProblemOption(99999L, 0, "업데이트하려는 설명", null);
        });

        getProblemOptionTest();
    }

}
