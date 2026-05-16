package com.escaperfecto.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameResult {
    private final int id;
    private final String questionPlayer;
    private final String cagePlayer;
    private final int totalPrize;
    private final boolean escaped;
    private final LocalDateTime date;
    private final int answeredQuestions;
    private final boolean failed;
    private final boolean safeEscapeUsed;
    private final String finishReason;
    private final List<Prize> takenPrizes;

    public GameResult(String questionPlayer, String cagePlayer, int totalPrize, boolean escaped, LocalDateTime date) {
        this(0, questionPlayer, cagePlayer, totalPrize, escaped, date, 0, false, false, "Partida finalizada", List.of());
    }

    public GameResult(String questionPlayer, String cagePlayer, int totalPrize, boolean escaped, LocalDateTime date,
                      int answeredQuestions, boolean failed, boolean safeEscapeUsed, String finishReason,
                      List<Prize> takenPrizes) {
        this(0, questionPlayer, cagePlayer, totalPrize, escaped, date, answeredQuestions, failed, safeEscapeUsed,
                finishReason, takenPrizes);
    }

    public GameResult(int id, String questionPlayer, String cagePlayer, int totalPrize, boolean escaped,
                      LocalDateTime date, int answeredQuestions, boolean failed, boolean safeEscapeUsed,
                      String finishReason, List<Prize> takenPrizes) {
        this.id = id;
        this.questionPlayer = questionPlayer;
        this.cagePlayer = cagePlayer;
        this.totalPrize = totalPrize;
        this.escaped = escaped;
        this.date = date;
        this.answeredQuestions = answeredQuestions;
        this.failed = failed;
        this.safeEscapeUsed = safeEscapeUsed;
        this.finishReason = finishReason;
        this.takenPrizes = new ArrayList<>(takenPrizes);
    }

    public int getId() {
        return id;
    }

    public String getQuestionPlayer() {
        return questionPlayer;
    }

    public String getCagePlayer() {
        return cagePlayer;
    }

    public int getTotalPrize() {
        return totalPrize;
    }

    public boolean isEscaped() {
        return escaped;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isSafeEscapeUsed() {
        return safeEscapeUsed;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public List<Prize> getTakenPrizes() {
        return new ArrayList<>(takenPrizes);
    }
}

