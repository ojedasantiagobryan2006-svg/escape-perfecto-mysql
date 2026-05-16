package com.escaperfecto;

import com.escaperfecto.db.DatabaseInitializer;
import com.escaperfecto.repository.JdbcGameRepository;
import com.escaperfecto.repository.JdbcPrizeRepository;
import com.escaperfecto.repository.JdbcQuestionRepository;
import com.escaperfecto.service.GameService;
import com.escaperfecto.ui.GameFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseInitializer initializer = new DatabaseInitializer();
                initializer.initialize();

                GameService gameService = new GameService(
                        new JdbcQuestionRepository(),
                        new JdbcPrizeRepository(),
                        new JdbcGameRepository()
                );

                GameFrame frame = new GameFrame(gameService);
                frame.setVisible(true);
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo iniciar Escape Perfecto.\n\n"
                                + "Revisa que MySQL este encendido y que las variables DB_USER, DB_PASSWORD, "
                                + "DB_HOST_URL y DB_NAME sean correctas.\n\n"
                                + "Detalle: " + exception.getMessage(),
                        "Error de inicio",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}

