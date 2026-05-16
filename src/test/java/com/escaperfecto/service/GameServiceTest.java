package com.escaperfecto.service;

import com.escaperfecto.model.GameResult;
import com.escaperfecto.model.Prize;
import com.escaperfecto.model.Question;
import com.escaperfecto.repository.GameRepository;
import com.escaperfecto.repository.PrizeRepository;
import com.escaperfecto.repository.QuestionRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameServiceTest {
    @Test
    void correctAnswerAddsSecondsAndAdvancesQuestion() {
        MemoryGameRepository gameRepository = new MemoryGameRepository();
        GameService service = new GameService(questionRepository(), prizeRepository(), gameRepository);

        service.startGame("Ana", "Luis", "Todas");
        Question firstQuestion = service.getCurrentQuestion();

        assertNotNull(firstQuestion);
        assertTrue(service.answerCurrentQuestion(firstQuestion.getCorrectAnswer()));
        assertEquals(firstQuestion.getSeconds(), service.getSeconds());
        assertEquals(1, service.getAnsweredQuestions());
    }

    @Test
    void cannotTakeSamePrizeTwice() {
        MemoryGameRepository gameRepository = new MemoryGameRepository();
        GameService service = new GameService(questionRepository(), prizeRepository(), gameRepository);
        Prize prize = prizeRepository().findAll().get(0);

        service.startGame("Ana", "Luis");
        service.answerCurrentQuestion(service.getCurrentQuestion().getCorrectAnswer());
        service.answerCurrentQuestion(service.getCurrentQuestion().getCorrectAnswer());

        assertTrue(service.takePrize(prize));
        assertFalse(service.takePrize(prize));
    }

    @Test
    void trappedTeamLosesPrizeTotalWhenGameFinishes() {
        MemoryGameRepository gameRepository = new MemoryGameRepository();
        GameService service = new GameService(questionRepository(), prizeRepository(), gameRepository);
        Prize prize = prizeRepository().findAll().get(0);

        service.startGame("Ana", "Luis");
        service.answerCurrentQuestion(service.getCurrentQuestion().getCorrectAnswer());
        service.answerCurrentQuestion(service.getCurrentQuestion().getCorrectAnswer());
        service.takePrize(prize);
        GameResult result = service.finishGame(false, false, false, "Prueba atrapado");

        assertFalse(result.isEscaped());
        assertEquals(0, result.getTotalPrize());
        assertEquals(2, result.getAnsweredQuestions());
        assertEquals("Prueba atrapado", result.getFinishReason());
        assertEquals(1, result.getTakenPrizes().size());
        assertEquals(1, gameRepository.savedResults.size());
    }

    @Test
    void safeEscapeKeepsTakenPrizeTotal() {
        MemoryGameRepository gameRepository = new MemoryGameRepository();
        GameService service = new GameService(questionRepository(), prizeRepository(), gameRepository);
        Prize normalPrize = prizeRepository().findAll().get(0);
        Prize safeEscape = prizeRepository().findAll().get(1);

        service.startGame("Ana", "Luis");
        answerCorrectQuestions(service, 6);
        service.takePrize(normalPrize);
        service.takePrize(safeEscape);
        GameResult result = service.finishGame(true);

        assertTrue(result.isEscaped());
        assertTrue(result.isSafeEscapeUsed());
        assertEquals(normalPrize.getValue(), result.getTotalPrize());
    }

    @Test
    void safeEscapeIsAvailableOnlyFromQuestionSeven() {
        MemoryGameRepository gameRepository = new MemoryGameRepository();
        GameService service = new GameService(questionRepository(), prizeRepository(), gameRepository);
        Prize safeEscape = prizeRepository().findAll().get(1);

        service.startGame("Ana", "Luis");
        answerCorrectQuestions(service, 5);

        assertFalse(service.canUseSafeEscape());
        assertFalse(service.takePrize(safeEscape));

        service.answerCurrentQuestion(service.getCurrentQuestion().getCorrectAnswer());

        assertTrue(service.canUseSafeEscape());
        assertTrue(service.takePrize(safeEscape));
    }

    @Test
    void selectedCategoryQuestionsArePrioritized() {
        MemoryGameRepository gameRepository = new MemoryGameRepository();
        GameService service = new GameService(questionRepository(), prizeRepository(), gameRepository);

        service.startGame("Ana", "Luis", "Ciencia");

        assertEquals("Ciencia", service.getCurrentQuestion().getCategory());
        service.changeQuestion();
        assertEquals("Ciencia", service.getCurrentQuestion().getCategory());
    }

    private QuestionRepository questionRepository() {
        return new QuestionRepository() {
            @Override
            public List<Question> findAll() {
                return List.of(
                        new Question(1, "Historia", "Pregunta 1", "A", "B", "C", "A", 10),
                        new Question(2, "Historia", "Pregunta 2", "A", "B", "C", "B", 10),
                        new Question(3, "Ciencia", "Pregunta 3", "A", "B", "C", "C", 10),
                        new Question(4, "Ciencia", "Pregunta 4", "A", "B", "C", "A", 10),
                        new Question(5, "Historia", "Pregunta 5", "A", "B", "C", "B", 10),
                        new Question(6, "Historia", "Pregunta 6", "A", "B", "C", "C", 10),
                        new Question(7, "Ciencia", "Pregunta 7", "A", "B", "C", "A", 10),
                        new Question(8, "Ciencia", "Pregunta 8", "A", "B", "C", "B", 10)
                );
            }

            @Override
            public void add(Question question) {
            }

            @Override
            public void update(Question question) {
            }

            @Override
            public void deleteById(int id) {
            }
        };
    }

    private PrizeRepository prizeRepository() {
        return new PrizeRepository() {
            @Override
            public List<Prize> findAll() {
                return List.of(
                        new Prize(1, "Tablet", 3500, 10, false),
                        new Prize(2, "Escape seguro", 0, 6, true)
                );
            }

            @Override
            public void add(Prize prize) {
            }

            @Override
            public void update(Prize prize) {
            }

            @Override
            public void deleteById(int id) {
            }
        };
    }

    private void answerCorrectQuestions(GameService service, int amount) {
        for (int i = 0; i < amount; i++) {
            service.answerCurrentQuestion(service.getCurrentQuestion().getCorrectAnswer());
        }
    }

    private static class MemoryGameRepository implements GameRepository {
        private final List<GameResult> savedResults = new ArrayList<>();

        @Override
        public void save(GameResult result) {
            savedResults.add(result);
        }

        @Override
        public List<GameResult> findLastResults() {
            return savedResults;
        }
    }
}
