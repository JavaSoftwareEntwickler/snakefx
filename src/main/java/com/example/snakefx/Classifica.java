package com.example.snakefx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Classifica {
    private static final String DB_URL = "jdbc:sqlite:classifica.db";

    public Classifica() {
        // Inizializza il database
        creaTabella();
    }

    private void creaTabella() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS classifica (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    punteggio INTEGER NOT NULL
                );
                """;
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void aggiungiGiocatore(Giocatore giocatore) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO classifica (nome, punteggio) VALUES (?, ?)")) {
            pstmt.setString(1, giocatore.getNome());
            pstmt.setInt(2, giocatore.getPunteggio());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Giocatore> caricaClassifica() {
        List<Giocatore> classifica = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, punteggio FROM classifica ORDER BY punteggio DESC")) {
            while (rs.next()) {
                String nome = rs.getString("nome");
                int punteggio = rs.getInt("punteggio");
                classifica.add(new Giocatore(nome, punteggio));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classifica;
    }
    public String getClassifica() {
        List<Giocatore> listaClassifica = this.caricaClassifica();
        StringBuilder classificaTesto = new StringBuilder("Classifica:\n");
        for (int i = 0; i < Math.min(10, listaClassifica.size()); i++) {
            Giocatore g = listaClassifica.get(i);
            classificaTesto.append(i + 1).append(". ").append(g).append("\n");
        }
        return classificaTesto.toString();
    }
}
