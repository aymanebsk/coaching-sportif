package com.coaching.dao;

import com.coaching.config.DatabaseConnection;
import com.coaching.model.SuiviProgression;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuiviProgressionDAO {

    public void create(SuiviProgression sp) throws SQLException {
        String sql = "INSERT INTO suivi_progression (sportif_id, date_mesure, poids_kg, tour_taille_cm, commentaire) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sp.getSportifId());
            stmt.setDate(2, Date.valueOf(sp.getDateMesure()));
            stmt.setDouble(3, sp.getPoidsKg());
            stmt.setDouble(4, sp.getTourTailleCm());
            stmt.setString(5, sp.getCommentaire());
            stmt.executeUpdate();
        }
    }

    public List<SuiviProgression> findBySportifId(int sportifId) throws SQLException {
        List<SuiviProgression> liste = new ArrayList<>();
        String sql = "SELECT * FROM suivi_progression WHERE sportif_id = ? ORDER BY date_mesure ASC, id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sportifId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SuiviProgression sp = new SuiviProgression();
                    sp.setId(rs.getInt("id"));
                    sp.setSportifId(rs.getInt("sportif_id"));
                    sp.setDateMesure(rs.getDate("date_mesure").toLocalDate());
                    sp.setPoidsKg(rs.getDouble("poids_kg"));
                    sp.setTourTailleCm(rs.getDouble("tour_taille_cm"));
                    sp.setCommentaire(rs.getString("commentaire"));
                    liste.add(sp);
                }
            }
        }
        return liste;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM suivi_progression WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}