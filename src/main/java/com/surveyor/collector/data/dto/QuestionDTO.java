package com.surveyor.collector.data.dto;

public class QuestionDTO {
    private String questionId;

    private String answerId;

    public QuestionDTO() {
    }

    public QuestionDTO(String questionId, String answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    @Override
    public String toString() {
        return "QuestionDTO{" +
                "questionId='" + questionId + '\'' +
                ", answerId='" + answerId + '\'' +
                '}';
    }
}
