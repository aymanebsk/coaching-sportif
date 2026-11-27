package com.coaching.dao;

import com.coaching.config.DatabaseConnection;
import com.coaching.model.SuiviNutrition;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuiviNutritionDAO {

    public void create(SuiviNutrition sn) throws SQLException {
        String sql = "INSERT INTO suivi_nutrition (sportif_id, date_repas, type_repas, calories, proteines_g) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sn.getSportifId());
            stmt.setDate(2, Date.valueOf(sn.getDateRepas()));
            stmt.setString(3, sn.getTypeRepas());
            stmt.setInt(4, sn.getCalories());
            stmt.setDouble(5, sn.getProteinesG());
            stmt.executeUpdate();
        }
    }

    public List<SuiviNutrition> findBySportifId(int sportifId) throws SQLException {
        List<SuiviNutrition> liste = new ArrayList<>();
        String sql = "SELECT * FROM suivi_nutrition WHERE sportif_id = ? ORDER BY date_repas DESC, id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sportifId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SuiviNutrition sn = new SuiviNutrition();
                    sn.setId(rs.getInt("id"));
                    sn.setSportifId(rs.getInt("sportif_id"));
                    sn.setDateRepas(rs.getDate("date_repas").toLocalDate());
                    sn.setTypeRepas(rs.getString("type_repas"));
                    sn.setCalories(rs.getInt("calories"));
                    sn.setProteinesG(rs.getDouble("proteines_g"));
                    liste.add(sn);
                }
            }
        }
        return liste;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM suivi_nutrition WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}