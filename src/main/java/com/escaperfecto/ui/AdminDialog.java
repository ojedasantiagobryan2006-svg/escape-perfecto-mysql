package com.escaperfecto.ui;

import com.escaperfecto.model.Prize;
import com.escaperfecto.model.Question;
import com.escaperfecto.service.GameService;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;

public class AdminDialog extends JDialog {
    private final GameService gameService;
    private final DefaultTableModel questionModel = new DefaultTableModel(
            new Object[]{"ID", "Categoria", "Pregunta", "A", "B", "C", "Correcta", "Segundos"}, 0
    );
    private final DefaultTableModel prizeModel = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Valor", "Segundos", "Escape seguro"}, 0
    );
    private final JTable questionTable = new JTable(questionModel);
    private final JTable prizeTable = new JTable(prizeModel);

    public AdminDialog(Frame owner, GameService gameService) {
        super(owner, "Modo administrador", true);
        this.gameService = gameService;
        setSize(900, 520);
        setLocationRelativeTo(owner);
        buildLayout();
        reloadTables();
    }

    private void buildLayout() {
        var tabs = new javax.swing.JTabbedPane();
        tabs.addTab("Preguntas", buildQuestionPanel());
        tabs.addTab("Premios", buildPrizePanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(questionTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(1, 3, 8, 8));
        JButton addButton = new JButton("Agregar");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Borrar");
        addButton.addActionListener(event -> addQuestion());
        editButton.addActionListener(event -> editQuestion());
        deleteButton.addActionListener(event -> deleteQuestion());
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildPrizePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(prizeTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(1, 3, 8, 8));
        JButton addButton = new JButton("Agregar");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Borrar");
        addButton.addActionListener(event -> addPrize());
        editButton.addActionListener(event -> editPrize());
        deleteButton.addActionListener(event -> deletePrize());
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void reloadTables() {
        questionModel.setRowCount(0);
        for (Question question : gameService.getAllQuestions()) {
            questionModel.addRow(new Object[]{
                    question.getId(), question.getCategory(), question.getText(), question.getOptionA(),
                    question.getOptionB(), question.getOptionC(), question.getCorrectAnswer(), question.getSeconds()
            });
        }

        prizeModel.setRowCount(0);
        for (Prize prize : gameService.getAllPrizes()) {
            prizeModel.addRow(new Object[]{
                    prize.getId(), prize.getName(), prize.getValue(), prize.getSecondsToTake(),
                    prize.isSafeEscape() ? "Si" : "No"
            });
        }
    }

    private void addQuestion() {
        Question question = showQuestionForm(null);
        if (question != null) {
            gameService.addQuestion(question);
            reloadTables();
        }
    }

    private void editQuestion() {
        int row = questionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una pregunta.");
            return;
        }
        Question selected = questionFromRow(row);
        Question updated = showQuestionForm(selected);
        if (updated != null) {
            gameService.updateQuestion(updated);
            reloadTables();
        }
    }

    private void deleteQuestion() {
        int row = questionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una pregunta.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Borrar pregunta seleccionada?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gameService.deleteQuestion((Integer) questionModel.getValueAt(row, 0));
            reloadTables();
        }
    }

    private Question showQuestionForm(Question original) {
        JTextField category = new JTextField(original == null ? "General" : original.getCategory());
        JTextField text = new JTextField(original == null ? "" : original.getText());
        JTextField optionA = new JTextField(original == null ? "" : original.getOptionA());
        JTextField optionB = new JTextField(original == null ? "" : original.getOptionB());
        JTextField optionC = new JTextField(original == null ? "" : original.getOptionC());
        JComboBox<String> correct = new JComboBox<>(new String[]{"A", "B", "C"});
        JSpinner seconds = new JSpinner(new SpinnerNumberModel(original == null ? 10 : original.getSeconds(), 1, 120, 1));
        if (original != null) {
            correct.setSelectedItem(original.getCorrectAnswer());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Categoria"));
        panel.add(category);
        panel.add(new JLabel("Pregunta"));
        panel.add(text);
        panel.add(new JLabel("Opcion A"));
        panel.add(optionA);
        panel.add(new JLabel("Opcion B"));
        panel.add(optionB);
        panel.add(new JLabel("Opcion C"));
        panel.add(optionC);
        panel.add(new JLabel("Correcta"));
        panel.add(correct);
        panel.add(new JLabel("Segundos"));
        panel.add(seconds);

        int option = JOptionPane.showConfirmDialog(this, panel, "Pregunta", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return null;
        }
        if (text.getText().isBlank() || optionA.getText().isBlank() || optionB.getText().isBlank()
                || optionC.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos de la pregunta.");
            return null;
        }
        int id = original == null ? 0 : original.getId();
        return new Question(id, category.getText().trim(), text.getText().trim(), optionA.getText().trim(),
                optionB.getText().trim(), optionC.getText().trim(), String.valueOf(correct.getSelectedItem()),
                (Integer) seconds.getValue());
    }

    private Question questionFromRow(int row) {
        return new Question(
                (Integer) questionModel.getValueAt(row, 0),
                String.valueOf(questionModel.getValueAt(row, 1)),
                String.valueOf(questionModel.getValueAt(row, 2)),
                String.valueOf(questionModel.getValueAt(row, 3)),
                String.valueOf(questionModel.getValueAt(row, 4)),
                String.valueOf(questionModel.getValueAt(row, 5)),
                String.valueOf(questionModel.getValueAt(row, 6)),
                (Integer) questionModel.getValueAt(row, 7)
        );
    }

    private void addPrize() {
        Prize prize = showPrizeForm(null);
        if (prize != null) {
            gameService.addPrize(prize);
            reloadTables();
        }
    }

    private void editPrize() {
        int row = prizeTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un premio.");
            return;
        }
        Prize selected = prizeFromRow(row);
        Prize updated = showPrizeForm(selected);
        if (updated != null) {
            gameService.updatePrize(updated);
            reloadTables();
        }
    }

    private void deletePrize() {
        int row = prizeTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un premio.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Borrar premio seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gameService.deletePrize((Integer) prizeModel.getValueAt(row, 0));
            reloadTables();
        }
    }

    private Prize showPrizeForm(Prize original) {
        JTextField name = new JTextField(original == null ? "" : original.getName());
        JSpinner value = new JSpinner(new SpinnerNumberModel(original == null ? 1000 : original.getValue(), 0, 1000000, 100));
        JSpinner seconds = new JSpinner(new SpinnerNumberModel(original == null ? 5 : original.getSecondsToTake(), 1, 120, 1));
        JCheckBox safeEscape = new JCheckBox("Escape seguro", original != null && original.isSafeEscape());

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Nombre"));
        panel.add(name);
        panel.add(new JLabel("Valor"));
        panel.add(value);
        panel.add(new JLabel("Segundos"));
        panel.add(seconds);
        panel.add(new JLabel(""));
        panel.add(safeEscape);

        int option = JOptionPane.showConfirmDialog(this, panel, "Premio", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return null;
        }
        if (name.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Escribe el nombre del premio.");
            return null;
        }
        int id = original == null ? 0 : original.getId();
        return new Prize(id, name.getText().trim(), (Integer) value.getValue(), (Integer) seconds.getValue(),
                safeEscape.isSelected());
    }

    private Prize prizeFromRow(int row) {
        return new Prize(
                (Integer) prizeModel.getValueAt(row, 0),
                String.valueOf(prizeModel.getValueAt(row, 1)),
                (Integer) prizeModel.getValueAt(row, 2),
                (Integer) prizeModel.getValueAt(row, 3),
                "Si".equals(String.valueOf(prizeModel.getValueAt(row, 4)))
        );
    }
}
