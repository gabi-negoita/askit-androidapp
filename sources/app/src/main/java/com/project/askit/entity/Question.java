package com.project.askit.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Question implements Serializable {

    private int id;
    private String subject;
    private String htmlText;
    private Timestamp createdDate;
    private int categoryId;
    private int userId;
    private int approved;
//    private QuestionStatistics questionStatistics;

    public Question() {
    }

    public Question(String subject, String htmlText, Timestamp createdDate, int categoryId, int userId, int approved) {
        this.subject = subject;
        this.htmlText = htmlText;
        this.createdDate = createdDate;
        this.categoryId = categoryId;
        this.userId = userId;
        this.approved = approved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

//    public QuestionStatistics getQuestionStatistics() {
//        return questionStatistics;
//    }
//
//    public void setQuestionStatistics(QuestionStatistics questionStatistics) {
//        this.questionStatistics = questionStatistics;
//    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", htmlText='" + htmlText + '\'' +
                ", createdDate=" + createdDate +
                ", categoryId=" + categoryId +
                ", userId=" + userId +
                ", approved=" + approved +
//                ", questionStatistics=" + questionStatistics +
                '}';
    }
}
