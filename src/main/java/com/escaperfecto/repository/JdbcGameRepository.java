package com.escaperfecto.repository;

import com.escaperfecto.db.Database;
import com.escaperfecto.model.GameResult;
import com.escaperfecto.model.Prize;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcGameRepository implements GameRepository {
    @Override
    public void save(GameResult result) {
        String sql = """
                INSERT INTO partidas (
                    jugador_preguntas, jugador_jaula, premio_total, escapo, fecha,
                    preguntas_contestadas, fallo, uso_escape_seguro, motivo_fin
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, result.getQuestionPlayer());
            statement.setString(2, result.getCagePlayer());
            statement.setInt(3, result.getTotalPrize());
            statement.setBoolean(4, result.isEscaped());
            statement.setString(5, result.getDate().toString());
            statement.setInt(6, result.getAnsweredQuestions());
            statement.setBoolean(7, result.isFailed());
            statement.setBoolean(8, result.isSafeEscapeUsed());
            statement.setString(9, result.getFinishReason());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    savePrizes(connection, generatedKeys.getInt(1), result.getTakenPrizes());
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo guardar la partida.", exception);
        }
    }

    @Override
    public List<GameResult> findLastResults() {
        List<GameResult> results = new ArrayList<>();
        String sql = """
                SELECT id, jugador_preguntas, jugador_jaula, premio_total, escapo, fecha,
                       preguntas_contestadas, fallo, uso_escape_seguro, motivo_fin
                FROM partidas
                ORDER BY id DESC
                LIMIT 8
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int gameId = resultSet.getInt("id");
                results.add(new GameResult(
                        gameId,
                        resultSet.getString("jugador_preguntas"),
                        resultSet.getString("jugador_jaula"),
                        resultSet.getInt("premio_total"),
                        resultSet.getBoolean("escapo"),
                        LocalDateTime.parse(resultSet.getString("fecha")),
                        resultSet.getInt("preguntas_contestadas"),
                        resultSet.getBoolean("fallo"),
                        resultSet.getBoolean("uso_escape_seguro"),
                        resultSet.getString("motivo_fin"),
                        findPrizesByGameId(connection, gameId)
                ));
            }
            return results;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar el historial.", exception);
        }
    }

    private void savePrizes(Connection connection, int gameId, List<Prize> prizes) throws SQLException {
        String sql = """
                INSERT INTO partida_premios (partida_id, premio_id, nombre, valor, segundos_para_tomar)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Prize prize : prizes) {
                statement.setInt(1, gameId);
                statement.setInt(2, prize.getId());
                statement.setString(3, prize.getName());
                statement.setInt(4, prize.getValue());
                statement.setInt(5, prize.getSecondsToTake());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private List<Prize> findPrizesByGameId(Connection connection, int gameId) throws SQLException {
        List<Prize> prizes = new ArrayList<>();
        String sql = """
                SELECT premio_id, nombre, valor, segundos_para_tomar
                FROM partida_premios
                WHERE partida_id = ?
                ORDER BY id
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    prizes.add(new Prize(
                            resultSet.getInt("premio_id"),
                            resultSet.getString("nombre"),
                            resultSet.getInt("valor"),
                            resultSet.getInt("segundos_para_tomar"),
                            false
                    ));
                }
            }
        }
        return prizes;
    }
}
