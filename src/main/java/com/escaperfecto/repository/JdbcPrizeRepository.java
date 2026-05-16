package com.escaperfecto.repository;

import com.escaperfecto.db.Database;
import com.escaperfecto.model.Prize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPrizeRepository implements PrizeRepository {
    @Override
    public List<Prize> findAll() {
        List<Prize> prizes = new ArrayList<>();
        String sql = "SELECT id, nombre, valor, segundos_para_tomar, escape_seguro FROM premios";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                prizes.add(new Prize(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getInt("valor"),
                        resultSet.getInt("segundos_para_tomar"),
                        resultSet.getBoolean("escape_seguro")
                ));
            }
            return prizes;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudieron cargar los premios.", exception);
        }
    }

    @Override
    public void add(Prize prize) {
        String sql = "INSERT INTO premios (nombre, valor, segundos_para_tomar, escape_seguro) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, prize);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo agregar el premio.", exception);
        }
    }

    @Override
    public void update(Prize prize) {
        String sql = """
                UPDATE premios
                SET nombre = ?, valor = ?, segundos_para_tomar = ?, escape_seguro = ?
                WHERE id = ?
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, prize);
            statement.setInt(5, prize.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar el premio.", exception);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM premios WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo borrar el premio.", exception);
        }
    }

    private void fillStatement(PreparedStatement statement, Prize prize) throws SQLException {
        statement.setString(1, prize.getName());
        statement.setInt(2, prize.getValue());
        statement.setInt(3, prize.getSecondsToTake());
        statement.setBoolean(4, prize.isSafeEscape());
    }
}
