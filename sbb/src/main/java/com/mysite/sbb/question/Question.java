package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    private LocalDateTime createDate;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    // 연쇄 삭제, 답변 부터 삭제
    // Answer_ID_List같은 칼럼은 생기지 않는다.
     @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    // EAGER는 성능에 좋지 않다. LAZY로 변경하는 것이 좋다.
    // @OneToMany(mappedBy = "question", fetch = EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Answer> answers = new ArrayList<>();

    public Answer addAnswer(String content) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(this);
        answer.setCreateDate(LocalDateTime.now());

        answers.add(answer);
        // cascade로 인해 리포지토리를 통하지 않고 바로 DB에 저장할 수 있다
        //Transcction이 끝나면 반영된다.
        return answer;
    }

}
