package com.escaperfecto.service;

import com.escaperfecto.model.GameResult;
import com.escaperfecto.model.Prize;
import com.escaperfecto.model.Question;
import com.escaperfecto.repository.GameRepository;
import com.escaperfecto.repository.PrizeRepository;
import com.escaperfecto.repository.QuestionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GameService {
    private static final int QUESTION_LIMIT = 10;
    private static final int SAFE_ESCAPE_MINIMUM_QUESTION = 7;
    private static final Set<Integer> RECENT_QUESTION_IDS = new HashSet<>();
    private final QuestionRepository questionRepository;
    private final PrizeRepository prizeRepository;
    private final GameRepository gameRepository;
    private List<Question> questions = new ArrayList<>();
    private List<Question> allQuestions = new ArrayList<>();
    private List<Prize> prizes = new ArrayList<>();
    private int questionIndex;
    private int seconds;
    private int totalPrize;
    private boolean escaped;
    private String questionPlayer;
    private String cagePlayer;
    private Set<Integer> takenPrizeIds = new HashSet<>();
    private List<Prize> takenPrizes = new ArrayList<>();

    public GameService(QuestionRepository questionRepository, PrizeRepository prizeRepository, GameRepository gameRepository) {
        this.questionRepository = questionRepository;
        this.prizeRepository = prizeRepository;
        this.gameRepository = gameRepository;
    }

    public void startGame(String questionPlayer, String cagePlayer) {
        startGame(questionPlayer, cagePlayer, "Todas");
    }

    public void startGame(String questionPlayer, String cagePlayer, String category) {
        this.questionPlayer = questionPlayer;
        this.cagePlayer = cagePlayer;
        this.allQuestions = questionRepository.findAll();
        this.questions = buildQuestionRound(category);
        this.prizes = new ArrayList<>(prizeRepository.findAll());
        Collections.shuffle(this.prizes);
        this.questionIndex = 0;
        this.seconds = 0;
        this.totalPrize = 0;
        this.escaped = false;
        this.takenPrizeIds = new HashSet<>();
        this.takenPrizes = new ArrayList<>();
        rememberQuestions(this.questions);
    }

    public Question getCurrentQuestion() {
        if (questionIndex >= questions.size() || questionIndex >= QUESTION_LIMIT) {
            return null;
        }
        return questions.get(questionIndex);
    }

    public Question changeQuestion() {
        if (questionIndex < questions.size()) {
            questionIndex++;
        }
        return getCurrentQuestion();
    }

    public Question changeCategory(String category) {
        this.questions = buildQuestionRound(category);
        this.questionIndex = Math.min(questionIndex, questions.size());
        return getCurrentQuestion();
    }

    public boolean answerCurrentQuestion(String answer) {
        Question current = getCurrentQuestion();
        if (current == null) {
            return false;
        }

        boolean correct = current.isCorrect(answer);
        if (correct) {
            seconds += current.getSeconds();
        }
        questionIndex++;
        return correct;
    }

    public boolean takePrize(Prize prize) {
        if (prize == null || seconds < prize.getSecondsToTake() || escaped || takenPrizeIds.contains(prize.getId())) {
            return false;
        }
        if (prize.isSafeEscape() && !canUseSafeEscape()) {
            return false;
        }

        seconds -= prize.getSecondsToTake();
        takenPrizeIds.add(prize.getId());
        if (prize.isSafeEscape()) {
            escaped = true;
        } else {
            totalPrize += prize.getValue();
            takenPrizes.add(prize);
        }
        return true;
    }

    public void tickSecond() {
        if (seconds > 0) {
            seconds--;
        }
    }

    public GameResult finishGame(boolean escapedByDoor) {
        return finishGame(escapedByDoor, false, escapedByDoor, "Partida finalizada");
    }

    public GameResult finishGame(boolean escapedByDoor, boolean failed, boolean safeEscapeUsed, String finishReason) {
        boolean finalEscape = escaped || escapedByDoor;
        int finalPrize = finalEscape ? totalPrize : 0;
        GameResult result = new GameResult(
                questionPlayer,
                cagePlayer,
                finalPrize,
                finalEscape,
                LocalDateTime.now(),
                getAnsweredQuestions(),
                failed,
                safeEscapeUsed,
                finishReason,
                takenPrizes
        );
        gameRepository.save(result);
        return result;
    }

    public List<Prize> getPrizes() {
        return prizes;
    }

    public List<String> getCategories() {
        Set<String> categories = new LinkedHashSet<>();
        categories.add("Todas");
        for (Question question : questionRepository.findAll()) {
            categories.add(question.getCategory());
        }
        return new ArrayList<>(categories);
    }

    public int getSeconds() {
        return seconds;
    }

    public int getAnsweredQuestions() {
        return Math.min(questionIndex, QUESTION_LIMIT);
    }

    public int getQuestionLimit() {
        return QUESTION_LIMIT;
    }

    public boolean canUseSafeEscape() {
        return getAnsweredQuestions() >= SAFE_ESCAPE_MINIMUM_QUESTION - 1;
    }

    public int getSafeEscapeMinimumQuestion() {
        return SAFE_ESCAPE_MINIMUM_QUESTION;
    }

    public boolean hasQuestionsRemaining() {
        return getCurrentQuestion() != null;
    }

    public int getTotalPrize() {
        return totalPrize;
    }

    public List<Prize> getTakenPrizes() {
        return new ArrayList<>(takenPrizes);
    }

    public List<GameResult> getLastResults() {
        return gameRepository.findLastResults();
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public void addQuestion(Question question) {
        questionRepository.add(question);
    }

    public void updateQuestion(Question question) {
        questionRepository.update(question);
    }

    public void deleteQuestion(int id) {
        questionRepository.deleteById(id);
    }

    public List<Prize> getAllPrizes() {
        return prizeRepository.findAll();
    }

    public void addPrize(Prize prize) {
        prizeRepository.add(prize);
    }

    public void updatePrize(Prize prize) {
        prizeRepository.update(prize);
    }

    public void deletePrize(int id) {
        prizeRepository.deleteById(id);
    }

    private List<Question> buildQuestionRound(String category) {
        List<Question> selected = new ArrayList<>();
        List<Question> preferred = new ArrayList<>();
        List<Question> recent = new ArrayList<>();
        if (category == null || category.equals("Todas")) {
            splitByRecentUse(allQuestions, preferred, recent);
            Collections.shuffle(preferred);
            Collections.shuffle(recent);
            selected.addAll(preferred);
            selected.addAll(recent);
        } else {
            List<Question> categoryQuestions = new ArrayList<>();
            for (Question question : allQuestions) {
                if (question.getCategory().equals(category)) {
                    categoryQuestions.add(question);
                }
            }
            splitByRecentUse(categoryQuestions, preferred, recent);
            Collections.shuffle(preferred);
            Collections.shuffle(recent);
            selected.addAll(preferred);
            selected.addAll(recent);
            for (Question question : allQuestions) {
                if (selected.size() >= QUESTION_LIMIT) {
                    break;
                }
                if (!containsQuestion(selected, question.getId())) {
                    selected.add(question);
                }
            }
        }
        if (selected.size() > QUESTION_LIMIT) {
            return new ArrayList<>(selected.subList(0, QUESTION_LIMIT));
        }
        return selected;
    }

    private void splitByRecentUse(List<Question> source, List<Question> preferred, List<Question> recent) {
        for (Question question : source) {
            if (RECENT_QUESTION_IDS.contains(question.getId())) {
                recent.add(question);
            } else {
                preferred.add(question);
            }
        }
    }

    private void rememberQuestions(List<Question> selectedQuestions) {
        if (RECENT_QUESTION_IDS.size() + selectedQuestions.size() >= allQuestions.size()) {
            RECENT_QUESTION_IDS.clear();
        }
        for (Question question : selectedQuestions) {
            RECENT_QUESTION_IDS.add(question.getId());
        }
    }

    private boolean containsQuestion(List<Question> questions, int id) {
        for (Question question : questions) {
            if (question.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
