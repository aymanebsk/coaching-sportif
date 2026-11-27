package com.coaching.dao;

import com.coaching.config.DatabaseConnection;
import com.coaching.model.Programme;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgrammeDAO {

    public void create(Programme p) throws SQLException {
        String sql = "INSERT INTO programme (titre, description, niveau, duree_semaines, coach_id, sportif_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getTitre());
            stmt.setString(2, p.getDescription());
            stmt.setString(3, p.getNiveau());
            stmt.setInt(4, p.getDureeSemaines());
            stmt.setInt(5, p.getCoachId());
            stmt.setInt(6, p.getSportifId());
            stmt.executeUpdate();
        }
    }

    public List<Programme> findByCoachId(int coachId) throws SQLException {
        List<Programme> liste = new ArrayList<>();
        String sql = "SELECT * FROM programme WHERE coach_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, coachId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapRow(rs));
                }
            }
        }
        return liste;
    }

    public List<Programme> findBySportifId(int sportifId) throws SQLException {
        List<Programme> liste = new ArrayList<>();
        String sql = "SELECT * FROM programme WHERE sportif_id = ? ORDER BY id DESC";
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

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM programme WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Programme mapRow(ResultSet rs) throws SQLException {
        Programme p = new Programme();
        p.setId(rs.getInt("id"));
        p.setTitre(rs.getString("titre"));
        p.setDescription(rs.getString("description"));
        p.setNiveau(rs.getString("niveau"));
        p.setDureeSemaines(rs.getInt("duree_semaines"));
        p.setCoachId(rs.getInt("coach_id"));
        p.setSportifId(rs.getInt("sportif_id"));
        return p;
    }
}