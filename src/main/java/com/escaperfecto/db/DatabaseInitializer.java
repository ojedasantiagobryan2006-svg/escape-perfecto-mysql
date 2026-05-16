package com.escaperfecto.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public void initialize() {
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS preguntas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        categoria TEXT NOT NULL DEFAULT 'General',
                        texto TEXT NOT NULL,
                        opcion_a TEXT NOT NULL,
                        opcion_b TEXT NOT NULL,
                        opcion_c TEXT NOT NULL,
                        respuesta_correcta TEXT NOT NULL,
                        segundos INTEGER NOT NULL
                    )
                    """);
            addCategoryColumnIfMissing(statement);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS premios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        valor INTEGER NOT NULL,
                        segundos_para_tomar INTEGER NOT NULL,
                        escape_seguro INTEGER NOT NULL DEFAULT 0
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS partidas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        jugador_preguntas TEXT NOT NULL,
                        jugador_jaula TEXT NOT NULL,
                        premio_total INTEGER NOT NULL,
                        escapo INTEGER NOT NULL,
                        fecha TEXT NOT NULL
                    )
                    """);

            seedQuestions(connection);
            seedPrizes(connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo inicializar la base de datos.", exception);
        }
    }

    private void addCategoryColumnIfMissing(Statement statement) throws SQLException {
        try {
            statement.executeUpdate("ALTER TABLE preguntas ADD COLUMN categoria TEXT NOT NULL DEFAULT 'General'");
        } catch (SQLException exception) {
            if (!exception.getMessage().toLowerCase().contains("duplicate column")) {
                throw exception;
            }
        }
    }

    private void seedQuestions(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM preguntas");
        }

        String sql = """
                INSERT INTO preguntas (categoria, texto, opcion_a, opcion_b, opcion_c, respuesta_correcta, segundos)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            addQuestion(statement, "Historia", "En que anio cayo el Imperio Romano de Occidente?", "476 d.C.", "1492", "1789", "A", 10);
            addQuestion(statement, "Historia", "Quien fue el primer presidente de los Estados Unidos?", "Abraham Lincoln", "George Washington", "Thomas Jefferson", "B", 10);
            addQuestion(statement, "Historia", "Que acontecimiento de 1789 marca el inicio de la Edad Contemporanea?", "La Revolucion Francesa", "La Primera Guerra Mundial", "La caida de Constantinopla", "A", 12);
            addQuestion(statement, "Historia", "Que civilizacion antigua construyo Machu Picchu?", "Los mayas", "Los incas", "Los egipcios", "B", 10);
            addQuestion(statement, "Historia", "Quien fue conocida como La dama de hierro?", "Indira Gandhi", "Margaret Thatcher", "Marie Curie", "B", 8);

            addQuestion(statement, "Geografia", "Cual es el rio mas largo y caudaloso del mundo?", "Rio Nilo", "Rio Amazonas", "Rio Yangtse", "B", 12);
            addQuestion(statement, "Geografia", "Cual es la capital de Australia?", "Sydney", "Melbourne", "Canberra", "C", 10);
            addQuestion(statement, "Geografia", "Que pais tiene la mayor extension territorial del mundo?", "Canada", "Rusia", "China", "B", 10);
            addQuestion(statement, "Geografia", "En que continente se encuentra el desierto de Atacama?", "Africa", "America del Sur", "Asia", "B", 8);
            addQuestion(statement, "Geografia", "Cual es el oceano mas grande de la Tierra?", "Atlantico", "Indico", "Pacifico", "C", 10);

            addQuestion(statement, "Ciencia y Naturaleza", "Cual es el elemento quimico mas abundante en el universo?", "Oxigeno", "Hidrogeno", "Carbono", "B", 12);
            addQuestion(statement, "Ciencia y Naturaleza", "Que organo del cuerpo humano consume la mayor cantidad de energia?", "Corazon", "Cerebro", "Pulmones", "B", 10);
            addQuestion(statement, "Ciencia y Naturaleza", "Como se llama la fuerza que atrae los objetos al centro de la Tierra?", "Magnetismo", "Gravedad", "Friccion", "B", 8);
            addQuestion(statement, "Ciencia y Naturaleza", "Cual es el unico mamifero capaz de volar de forma activa?", "Ardilla voladora", "Murcielago", "Colugo", "B", 10);
            addQuestion(statement, "Ciencia y Naturaleza", "Que gas absorbe gran parte de la radiacion ultravioleta del sol?", "Ozono", "Nitrogeno", "Helio", "A", 12);

            addQuestion(statement, "Arte y Literatura", "Quien escribio Cien anios de soledad?", "Gabriel Garcia Marquez", "Mario Vargas Llosa", "Julio Cortazar", "A", 12);
            addQuestion(statement, "Arte y Literatura", "Que pintor renacentista es autor de La Gioconda?", "Miguel Angel", "Leonardo da Vinci", "Rafael", "B", 10);
            addQuestion(statement, "Arte y Literatura", "Como se llama el miedo irracional a los libros o a la lectura?", "Claustrofobia", "Bibliofobia", "Acrofobia", "B", 12);
            addQuestion(statement, "Arte y Literatura", "En que ciudad italiana esta el fresco La ultima cena?", "Roma", "Florencia", "Milan", "C", 10);
            addQuestion(statement, "Arte y Literatura", "Quien es el autor de Romeo y Julieta?", "William Shakespeare", "Charles Dickens", "Victor Hugo", "A", 8);

            addQuestion(statement, "Entretenimiento y Cultura Pop", "Que pelicula de 1997 dirigida por James Cameron gano 11 premios Oscar?", "Avatar", "Titanic", "Terminator 2", "B", 12);
            addQuestion(statement, "Entretenimiento y Cultura Pop", "De que banda era vocalista Freddie Mercury?", "The Beatles", "Queen", "Pink Floyd", "B", 10);
            addQuestion(statement, "Entretenimiento y Cultura Pop", "Quien creo el universo de Star Wars?", "George Lucas", "Steven Spielberg", "James Cameron", "A", 10);
            addQuestion(statement, "Entretenimiento y Cultura Pop", "Cual es el videojuego mas vendido de todos los tiempos?", "Minecraft", "Tetris", "Grand Theft Auto V", "A", 12);
            addQuestion(statement, "Entretenimiento y Cultura Pop", "Que banda surcoreana se convirtio en fenomeno mundial de K-pop?", "BTS", "Coldplay", "One Direction", "A", 8);

            addQuestion(statement, "Deportes", "Cada cuantos anios se celebran los Juegos Olimpicos de Verano?", "Cada 2 anios", "Cada 4 anios", "Cada 6 anios", "B", 8);
            addQuestion(statement, "Deportes", "Que tenista masculino tiene el record de mas Grand Slam ganados?", "Roger Federer", "Rafael Nadal", "Novak Djokovic", "C", 12);
            addQuestion(statement, "Deportes", "En que deporte se usa un objeto llamado puck o disco?", "Hockey sobre hielo", "Rugby", "Beisbol", "A", 10);
            addQuestion(statement, "Deportes", "Que seleccion ha ganado mas Copas del Mundo de la FIFA?", "Alemania", "Brasil", "Argentina", "B", 10);
            addQuestion(statement, "Deportes", "Cuanto mide una piscina olimpica oficial?", "25 metros", "50 metros", "100 metros", "B", 8);
        }
    }

    private void addQuestion(PreparedStatement statement, String category, String text, String optionA, String optionB,
                             String optionC, String correctAnswer, int seconds) throws SQLException {
        statement.setString(1, category);
        statement.setString(2, text);
        statement.setString(3, optionA);
        statement.setString(4, optionB);
        statement.setString(5, optionC);
        statement.setString(6, correctAnswer);
        statement.setInt(7, seconds);
        statement.executeUpdate();
    }

    private void seedPrizes(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM premios");
        }

        String sql = "INSERT INTO premios (nombre, valor, segundos_para_tomar, escape_seguro) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            addPrize(statement, "Audifonos", 800, 5, false);
            addPrize(statement, "Tablet", 3500, 10, false);
            addPrize(statement, "Bicicleta", 5000, 14, false);
            addPrize(statement, "Escape seguro", 0, 6, true);
            addPrize(statement, "Smartwatch", 1200, 6, false);
            addPrize(statement, "Consola de videojuegos", 7500, 18, false);
            addPrize(statement, "Laptop", 12000, 22, false);
            addPrize(statement, "Bocina bluetooth", 900, 5, false);
            addPrize(statement, "Camara deportiva", 2800, 9, false);
            addPrize(statement, "Mochila gamer", 1500, 7, false);
            addPrize(statement, "Tarjeta de regalo", 1000, 4, false);
            addPrize(statement, "Viaje sorpresa", 15000, 25, false);
            addPrize(statement, "Telefono celular", 9000, 20, false);
            addPrize(statement, "Pantalla LED", 10000, 21, false);
        }
    }

    private void addPrize(PreparedStatement statement, String name, int value, int seconds, boolean safeEscape) throws SQLException {
        statement.setString(1, name);
        statement.setInt(2, value);
        statement.setInt(3, seconds);
        statement.setInt(4, safeEscape ? 1 : 0);
        statement.executeUpdate();
    }
}
