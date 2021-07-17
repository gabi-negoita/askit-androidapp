package com.project.askit.model;

public class QuestionItemModel {

    private Integer questionId;
    private String categoryTitle;
    private String username;
    private String questionCreatedDate;
    private String questionSubject;
    private Integer votes;
    private Integer answers;

    public QuestionItemModel() {
    }

    public QuestionItemModel(Integer questionId, String categoryTitle, String username, String questionCreatedDate, String questionSubject, Integer votes, Integer answers) {
        this.questionId = questionId;
        this.categoryTitle = categoryTitle;
        this.username = username;
        this.questionCreatedDate = questionCreatedDate;
        this.questionSubject = questionSubject;
        this.votes = votes;
        this.answers = answers;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQuestionCreatedDate() {
        return questionCreatedDate;
    }

    public void setQuestionCreatedDate(String questionCreatedDate) {
        this.questionCreatedDate = questionCreatedDate;
    }

    public String getQuestionSubject() {
        return questionSubject;
    }

    public void setQuestionSubject(String questionSubject) {
        this.questionSubject = questionSubject;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Integer getAnswers() {
        return answers;
    }

    public void setAnswers(Integer answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuestionItemModel{" +
                "questionId=" + questionId +
                ", categoryTitle='" + categoryTitle + '\'' +
                ", username='" + username + '\'' +
                ", questionCreatedDate='" + questionCreatedDate + '\'' +
                ", questionSubject='" + questionSubject + '\'' +
                ", votes=" + votes +
                ", answers=" + answers +
                '}';
    }
}
