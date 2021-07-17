package com.project.askit.entity;

public class QuestionStatistics {

    private int votes;

    private int answers;

    public QuestionStatistics() {
    }

    public QuestionStatistics(int votes, int answers) {
        this.votes = votes;
        this.answers = answers;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuestionStatistics{" +
                "votes=" + votes +
                ", answers=" + answers +
                '}';
    }
}