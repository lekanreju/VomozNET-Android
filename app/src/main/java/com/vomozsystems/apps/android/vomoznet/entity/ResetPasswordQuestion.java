package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Created by leksrej on 7/10/17.
 */

public class ResetPasswordQuestion {
    private Integer questionId;
    private String question;

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
