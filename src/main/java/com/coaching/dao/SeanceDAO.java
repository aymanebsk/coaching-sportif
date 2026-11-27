package com.coaching.dao;

import com.coaching.config.DatabaseConnection;
import com.coaching.model.Seance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    public void create(Seance s) throws SQLException {
        String sql = "INSERT INTO seance (programme_id, titre, jour_semaine, duree_minutes, calories_estimees, statut) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getProgrammeId());
            stmt.setString(2, s.getTitre());
            stmt.setString(3, s.getJourSemaine());
            stmt.setInt(4, s.getDureeMinutes());
            stmt.setInt(5, s.getCaloriesEstimees());
            stmt.setString(6, s.getStatut() != null ? s.getStatut() : "A_FAIRE");
            stmt.executeUpdate();
        }
    }

    public List<Seance> findByProgrammeId(int programmeId) throws SQLException {
        List<Seance> liste = new ArrayList<>();
        String sql = "SELECT * FROM seance WHERE programme_id = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, programmeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapRow(rs));
                }
            }
        }
        return liste;
    }

    public List<Seance> findBySportifId(int sportifId) throws SQLException {
        List<Seance> liste = new ArrayList<>();
        String sql = """
            SELECT se.* FROM seance se
            JOIN programme p ON se.programme_id = p.id
            WHERE p.sportif_id = ?
            ORDER BY se.id ASC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sportifId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapRow(rs));
                }
            }
        }
        return liste;
    }

    public void updateStatut(int seanceId, String statut) throws SQLException {
        String sql = "UPDATE seance SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut);
            stmt.setInt(2, seanceId);
            stmt.executeUpdate();
        }
    }

    public void delete(int seanceId) throws SQLException {
        String sql = "DELETE FROM seance WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seanceId);
            stmt.executeUpdate();
        }
    }

    private Seance mapRow(ResultSet rs) throws SQLException {
        Seance s = new Seance();
        s.setId(rs.getInt("id"));
        s.setProgrammeId(rs.getInt("programme_id"));
        s.setTitre(rs.getString("titre"));
        s.setJourSemaine(rs.getString("jour_semaine"));
        s.setDureeMinutes(rs.getInt("duree_minutes"));
        s.setCaloriesEstimees(rs.getInt("calories_estimees"));
        s.setStatut(rs.getString("statut"));
        return s;
    }
}