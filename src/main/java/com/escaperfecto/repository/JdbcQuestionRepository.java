package com.escaperfecto.repository;

import com.escaperfecto.db.Database;
import com.escaperfecto.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcQuestionRepository implements QuestionRepository {
    @Override
    public List<Question> findAll() {
        List<Question> questions = new ArrayList<>();
        String sql = """
                SELECT id, categoria, texto, opcion_a, opcion_b, opcion_c, respuesta_correcta, segundos
                FROM preguntas
                ORDER BY categoria, id
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                questions.add(new Question(
                        resultSet.getInt("id"),
                        resultSet.getString("categoria"),
                        resultSet.getString("texto"),
                        resultSet.getString("opcion_a"),
                        resultSet.getString("opcion_b"),
                        resultSet.getString("opcion_c"),
                        resultSet.getString("respuesta_correcta"),
                        resultSet.getInt("segundos")
                ));
            }
            return questions;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudieron cargar las preguntas.", exception);
        }
    }

    @Override
    public void add(Question question) {
        String sql = """
                INSERT INTO preguntas (categoria, texto, opcion_a, opcion_b, opcion_c, respuesta_correcta, segundos)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, question);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo agregar la pregunta.", exception);
        }
    }

    @Override
    public void update(Question question) {
        String sql = """
                UPDATE preguntas
                SET categoria = ?, texto = ?, opcion_a = ?, opcion_b = ?, opcion_c = ?, respuesta_correcta = ?, segundos = ?
                WHERE id = ?
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, question);
            statement.setInt(8, question.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar la pregunta.", exception);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM preguntas WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo borrar la pregunta.", exception);
        }
    }

    private void fillStatement(PreparedStatement statement, Question question) throws SQLException {
        statement.setString(1, question.getCategory());
        statement.setString(2, question.getText());
        statement.setString(3, question.getOptionA());
        statement.setString(4, question.getOptionB());
        statement.setString(5, question.getOptionC());
        statement.setString(6, question.getCorrectAnswer().toUpperCase());
        statement.setInt(7, question.getSeconds());
    }
}
