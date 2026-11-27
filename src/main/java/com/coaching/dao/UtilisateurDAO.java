package com.coaching.dao;

import com.coaching.config.DatabaseConnection;
import com.coaching.model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public List<Utilisateur> findAllSportifs() throws SQLException {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE role = 'SPORTIF' ORDER BY nom ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                u.setAge(rs.getInt("age"));
                u.setTailleCm(rs.getDouble("taille_cm"));
                u.setObjectif(rs.getString("objectif"));
                liste.add(u);
            }
        }
        return liste;
    }
}