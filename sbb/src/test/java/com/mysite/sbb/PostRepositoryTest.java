package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional // 테스트는 기본적으로 트랜잭션을 사용하여 롤백한다.
class PostRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("findAll")
    void t1() {
        List<Question> questions = questionRepository.findAll();
        assertThat(questions).hasSize(2);

        Question question = questions.get(0);
        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("findById")
    void t2() {
        Question question = questionRepository.findById(1).get();
        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("findBySubject")
    void t3() {
        Question question = questionRepository.findBySubject("sbb가 무엇인가요?").get();
        // SELECT * FROM question WHERE subject = 'sbb가 무엇인가요?'
        assertThat(question.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findBySubjectAndContent")
    void t4() {
        Question question = questionRepository.findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.").get();
        assertThat(question.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findBySubjectLike")
    void t5() {
        Question question = questionRepository.findBySubjectLike("sbb%").get();

        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("modify")
    void t6() {
        Question question = questionRepository.findById(1).get();
        assertThat(question).isNotNull();

        question.setSubject("수정된 제목");
        questionRepository.save(question);

        Question foundQuestion = questionRepository.findBySubject("수정된 제목").get();
        assertThat(foundQuestion).isNotNull();
    }

    @Test
    @DisplayName("삭제")
    void t7() {
        assertThat(questionRepository.count()).isEqualTo(2);

        Question question = questionRepository.findById(1).get();
        questionRepository.delete(question);

        assertThat(questionRepository.count()).isEqualTo(1);
    }

//    @Test
//    @DisplayName("답변 생성")//oneToMany를 사용하지 않은 버전
//    @Transactional
//    void t8() {
//        Question question = questionRepository.findById(2).get();
//
//        Answer answer = new Answer();
//        answer.setContent("네 자동으로 생성됩니다.");
//        answer.setQuestion(question);
//        answer.setCreateDate(LocalDateTime.now());
//        answerRepository.save(answer);
//    }
//
//    @Test
//    @DisplayName("답변 생성 v2") //oneToMany를 사용한 버전
//    @Transactional
//    @Rollback(value = false)
//    void t8() {
//        Question question = questionRepository.findById(2).get();
//
//        question.addAnswer("네 자동으로 생성됩니다.");
//
//        assertThat(question.getAnswers()).hasSize(1);
//    }
    @Test
    @DisplayName("답변 생성")
    void t8() {
        Question question = questionRepository.findById(2).get();

        Answer answer = new Answer();
        answer.setContent("네 자동으로 생성됩니다.");
        answer.setQuestion(question);
        answer.setCreateDate(LocalDateTime.now());
        answerRepository.save(answer);

        assertThat(answer.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("답변 조회 oneToMany")//더티 채킹, oneToMany는 기본적으로 더티채킹 안됨, persist 지정해야 함
    void t9() {
        Question question = questionRepository.findById(2).get();

        int beforeCount = question.getAnswers().size();

        Answer newAnswer = question.addAnswer("네 자동으로 생성됩니다.");

        // 트랜잭션이 종료된 이후에 DB에 반영되기 때문에 현재는 일단 0으로 설정된다.
        assertThat(newAnswer.getId()).isEqualTo(null);

        int afterCount = question.getAnswers().size();

        assertThat(afterCount).isEqualTo(beforeCount + 1);
    }


    @Test
    @DisplayName("답변 조회")
    void t10() {
        Answer answer = answerRepository.findById(1).get();

        assertThat(answer.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("답변 조회 by oneToMany")
    void t11() {
        Question question = questionRepository.findById(2).get();

        List<Answer> answers = question.getAnswers();
        assertThat(answers).hasSize(1);

        Answer answer = answers.get(0);
        assertThat(answer.getContent()).isEqualTo("네 자동으로 생성됩니다.");
    }

    @Test
    @DisplayName("findAnswer by question")
    void t12() {
        Question question = questionRepository.findById(2).get();

        Answer answer1 = question.getAnswers().get(0);

        assertThat(answer1.getId()).isEqualTo(1);
    }


}
