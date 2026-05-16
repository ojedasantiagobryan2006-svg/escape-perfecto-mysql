package com.escaperfecto.model;

public class Question {
    private final int id;
    private final String category;
    private final String text;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String correctAnswer;
    private final int seconds;

    public Question(int id, String category, String text, String optionA, String optionB, String optionC, String correctAnswer, int seconds) {
        this.id = id;
        this.category = category;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
        this.seconds = seconds;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isCorrect(String answer) {
        return correctAnswer.equalsIgnoreCase(answer);
    }
}
